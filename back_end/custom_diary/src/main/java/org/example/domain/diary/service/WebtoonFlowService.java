package org.example.domain.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.*;
import org.example.domain.diary.entity.*;
import org.example.domain.diary.repository.*;
import org.example.domain.diary.dto.PanelSetDTO;
import org.example.domain.gallery.dto.WeeklyFolderDTO;
import org.example.domain.user.entity.ArtStyle;
import org.example.domain.user.entity.User;
import org.example.domain.user.repository.ArtStyleRepository;
import org.example.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebtoonFlowService {
    private final DiaryRepository diaryRepo;
    private final GenerationSessionRepository sessionRepo;
    private final PanelCandidateRepository candidateRepo;
    private final SessionChoiceRepository choiceRepo;
    private final SeedPreferenceRepository prefRepo;
    private final UserRepository userRepo;
    private final RestTemplate restTemplate;
    private final WeeklyWebtoonRepository weeklyRepo;
    private final ObjectMapper objectMapper;
    private final ArtStyleRepository artStyleRepo;

    @Value("${fastapi.prompt.url:http://localhost:8000/generate}")
    private String promptApiUrl;
    @Value("${webtoon.service.url}")
    private String webtoonServiceUrl;

    @Transactional
    public CreateWebtoonSessionDTO createSession(String userId, Long diaryId, int panelsCount) {
        // 1) 일기 & 사용자 로드
        Diary diary = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("Diary not found"));
        User user = diary.getUser();

        // 2) DB에서 gender, age 계산
        final String gender = user.getGender();
        LocalDate birthDate = user.getBirthDate();
        final String ageStr = String.valueOf(
                Period.between(birthDate, LocalDate.now()).getYears()
        );

        // 3) FastAPI로부터 PromptRequestDTO 리스트 추출
        List<PromptRequestDTO> prompts = extractPromptsViaFastApi(diary.getContent(), panelsCount);

        // 4) 최신 ArtStyle JSON → Map 변환
        ArtStyle artStyle = artStyleRepo.findTopByUserOrderByCreatedAtDesc(user);
        final Map<String,String> styleMap;
        if (artStyle != null) {
            try {
                styleMap = objectMapper.readValue(
                        artStyle.getPrompt(),
                        new TypeReference<Map<String,String>>() {}
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("ArtStyle JSON 파싱 실패", e);
            }
        } else {
            styleMap = Collections.emptyMap();
        }


        // 5) PromptRequestDTO → StructuredPrompt 변환
        List<StructuredPrompt> structured = prompts.stream().map(dto -> {
            StructuredPrompt sp = new StructuredPrompt();
            // charInfo 리스트 → Map
            List<String> ciList = dto.getCharInfo();
            Map<String,String> ciMap = new LinkedHashMap<>();
            ciMap.put("gender",     gender);
            ciMap.put("importance", ciList.size()>1
                    ? ciList.get(1)
                    : styleMap.getOrDefault("importance",""));
            ciMap.put("age",        ageStr);
            ciMap.put("kind",       ciList.size()>3
                    ? ciList.get(3)
                    : styleMap.getOrDefault("kind",""));
            ciMap.put("shape",      ciList.size()>4
                    ? ciList.get(4)
                    : styleMap.getOrDefault("shape",""));
            ciMap.put("movement",   ciList.size()>5
                    ? ciList.get(5)
                    : styleMap.getOrDefault("movement",""));
            ciMap.put("clothing",   ciList.size()>6
                    ? ciList.get(6)
                    : styleMap.getOrDefault("clothing",""));

            sp.setCharInfo(ciMap);
            sp.setBackground(dto.getBackground());
            sp.setCaption(dto.getCaption());
            return sp;
        }).collect(Collectors.toList());

        // 6) 선호 seed vs 랜덤 seed
        String preferredSeed = determinePreferredSeed(userId);
        String randomSeed    = selectRandomSeed();

        // 7) seed 태깅
        List<StructuredPrompt> preferredSet = applySeed(structured, preferredSeed);
        List<StructuredPrompt> randomSet    = applySeed(structured, randomSeed);

        // 8) GenerationSession 저장
        GenerationSession session = new GenerationSession();
        session.setId(UUID.randomUUID());
        session.setUser(userRepo.getReferenceById(userId));
        session.setDiary(diary);
        session.setCreatedAt(LocalDateTime.now());
        sessionRepo.save(session);

        // 9) 외부 웹툰 생성 API 호출
        PanelSetDTO[] preferredPanels = generatePanels(session.getId(), preferredSet);
        PanelSetDTO[] randomPanels    = generatePanels(session.getId(), randomSet);

        // 10) PanelCandidate 저장
        saveCandidates(session, preferredPanels);
        saveCandidates(session, randomPanels);

        // 11) 결과 DTO 반환
        CreateWebtoonSessionDTO out = new CreateWebtoonSessionDTO();
        out.setSessionId(session.getId());
        out.setSets(Arrays.asList(preferredPanels));
        return out;
    }

    /**
     * FastAPI에 일기 텍스트 + panelsCount 전송 → PromptRequestDTO 리스트로 리턴
     */
    private List<PromptRequestDTO> extractPromptsViaFastApi(String diaryText, int panelsCount) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> body = new HashMap<>();
        body.put("inputs",      diaryText);
        body.put("panelsCount", panelsCount);

        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, headers);
        String resp = restTemplate.postForObject(promptApiUrl, req, String.class);

        try {
            Map<String,Object> map = objectMapper.readValue(
                    resp, new TypeReference<Map<String,Object>>() {}
            );
            Object gen = map.get("generated_text");
            return objectMapper.convertValue(
                    gen,
                    new TypeReference<List<PromptRequestDTO>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출/파싱 오류: " + e.getMessage(), e);
        }
    }

    /** 유저가 가장 많이 선택한 seed 반환, 없으면 신규 UUID */
    private String determinePreferredSeed(String userId) {
        return prefRepo.findByUserId(userId).stream()
                .max(Comparator.comparingInt(SeedPreference::getChosenCnt))
                .map(pref -> String.valueOf(pref.getSeed()))  // Optional<String>
                .orElse(UUID.randomUUID().toString());
    }

    /** 전체 seed 중 무작위 선택, 없으면 신규 UUID */
    private String selectRandomSeed() {
        List<SeedPreference> all = prefRepo.findAll();
        if (all.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        long raw = all.get(new Random().nextInt(all.size())).getSeed();
        return String.valueOf(raw);
    }

    /** 각 StructuredPrompt.caption 앞에 “[seed] ” 태그를 붙입니다 */
    private List<StructuredPrompt> applySeed(
            List<StructuredPrompt> prompts,
            String seed
    ) {
        return prompts.stream().map(sp -> {
            sp.setCaption("[" + seed + "] " + sp.getCaption());
            return sp;
        }).collect(Collectors.toList());
    }

    /** 외부 웹툰 생성 서버에 SessionID + panels 전송 → PanelSetDTO[] 리턴 */
    private PanelSetDTO[] generatePanels(UUID sessionId, List<StructuredPrompt> panels) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> body = new HashMap<>();
        body.put("session_id", sessionId.toString());
        body.put("panels",      panels);

        ResponseEntity<PanelSetDTO[]> resp = restTemplate.postForEntity(
                webtoonServiceUrl + "/generate_sets",
                new HttpEntity<>(body, headers),
                PanelSetDTO[].class
        );
        return resp.getBody();
    }

    /** 생성된 PanelSetDTO를 PanelCandidate 엔티티로 저장 */
    private void saveCandidates(GenerationSession session, PanelSetDTO[] sets) {
        for (PanelSetDTO set : sets) {
            for (PanelDTO p : set.getPanels()) {
                PanelCandidate cand = new PanelCandidate();
                cand.setSession(session);
                cand.setSetId(set.getSetId());
                cand.setPanelIndex((short) p.getPanelIndex());
                cand.setSeed(p.getSeed());
                cand.setPromptEn(p.getPromptEn());
                cand.setImagePath(p.getImagePath());
                cand.setCreatedAt(LocalDateTime.now());
                candidateRepo.save(cand);
            }
        }
    }

    @Transactional
    public void selectWebtoon(SelectWebtoonDTO dto, String userId) {
        SessionChoice sc = new SessionChoice();
        sc.setSessionId(dto.getSessionId()); sc.setSelectedSet(dto.getSelectedSet());
        sc.setSelectedAt(LocalDateTime.now()); choiceRepo.save(sc);
        // update preferences
        candidateRepo.findAllBySessionId(dto.getSessionId()).forEach(c -> {
            SeedPreferenceKey key = new SeedPreferenceKey(); key.setUserId(userId); key.setSeed(c.getSeed());
            SeedPreference pref = prefRepo.findById(key).orElseGet(() -> {
                SeedPreference sp = new SeedPreference(); sp.setUserId(userId); sp.setSeed(c.getSeed()); return sp;
            });
            if (c.getSetId()==dto.getSelectedSet()) pref.setChosenCnt(pref.getChosenCnt()+1);
            else pref.setSkippedCnt(pref.getSkippedCnt()+1);
            pref.setLastUsed(LocalDateTime.now()); prefRepo.save(pref);
        });
    }

    @Transactional
    public WeeklyFolderDTO generateWeekly(String userId, int year, int month, int week) {
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        LocalDate start = firstOfMonth.plusWeeks(week - 1);
        LocalDate end = start.plusWeeks(1);
        LocalDateTime fromLdt = start.atStartOfDay();
        LocalDateTime toLdt = end.atStartOfDay();

        List<Diary> weekDiaries = diaryRepo.findAllByUserFirebaseUidAndCreatedAtBetween(userId, fromLdt, toLdt);
        if (weekDiaries.isEmpty()) {
            throw new IllegalArgumentException("선택 주차에 일기가 없습니다.");
        }

        List<String> prompts = weekDiaries.stream()
                .flatMap(d -> Arrays.stream(d.getContent().split("[.?!]\\s*")))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        Collections.shuffle(prompts);

        CreateWebtoonSessionDTO cs = createSession(userId, weekDiaries.get(0).getId(), 4);

        Map<String,Object> mergeReq = Map.of(
                "diary_id", cs.getSessionId().toString(),
                "width", 1024
        );
        String mergedUrl = restTemplate.postForObject(
                        webtoonServiceUrl + "/merge_panels", mergeReq, Map.class)
                .get("merged_image_url").toString();

        WeeklyWebtoon record = new WeeklyWebtoon();
        record.setUserId(userId);
        record.setSessionId(cs.getSessionId());
        record.setYear(year);
        record.setMonth(month);
        record.setWeek(week);
        record.setMergedImageUrl(mergedUrl);
        record.setCreatedAt(LocalDateTime.now());
        weeklyRepo.save(record);

        WeeklyFolderDTO dto = new WeeklyFolderDTO();
        dto.setYear(year);
        dto.setMonth(month);
        dto.setWeek(week);
        dto.setMergedImageUrl(mergedUrl);
        dto.setSessionId(cs.getSessionId());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }
}


