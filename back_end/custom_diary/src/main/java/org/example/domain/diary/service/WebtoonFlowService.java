package org.example.domain.diary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.*;
import org.example.domain.diary.entity.*;
import org.example.domain.diary.repository.*;
import org.example.domain.diary.dto.PanelSetDTO;
import org.example.domain.gallery.dto.WeeklyFolderDTO;
import org.example.domain.user.entity.User;
import org.example.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.*;
import java.time.temporal.ChronoUnit;
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

    private final PromptExtractionService promptService;

    @Value("${webtoon.service.url}")
    private String webtoonServiceUrl;

    @Transactional
    public CreateWebtoonSessionDTO createSession(String userId, Long diaryId, int panelsCount) {
        // 1) Load diary and user info
        Diary diary = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("Diary not found"));
        User user = diary.getUser();

        // 2) Compute gender & age from DB
        String gender = user.getGender();             // e.g. "M" or "F"
        LocalDate birthDate = user.getBirthDate();           // e.g. 1998-07-15
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 3) Extract the remaining structured fields from diary content
        List<StructuredPrompt> prompts = promptService
                .extractStructuredPrompts(diary.getContent(), panelsCount);

        // 4) Enrich each prompt's char_info with gender & age
        prompts.forEach(sp -> {
            sp.getCharInfo().put("gender", gender);
            sp.getCharInfo().put("age", String.valueOf(age));
        });

        // 5) Persist session
        GenerationSession session = new GenerationSession();
        session.setId(UUID.randomUUID());
        session.setUser(userRepo.getReferenceById(userId));
        session.setDiary(diary);
        session.setCreatedAt(LocalDateTime.now());
        sessionRepo.save(session);

        // 6) Call FastAPI to generate panel sets
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> body = new HashMap<>();
        body.put("session_id", session.getId().toString());

        List<Map<String,Object>> panels = prompts.stream().map(sp -> {
            Map<String,Object> m = new HashMap<>();
            m.put("char_info", sp.getCharInfo());
            m.put("background", sp.getBackground());
            m.put("caption", sp.getCaption());
            return m;
        }).collect(Collectors.toList());

        body.put("panels", panels);
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, headers);
        ResponseEntity<PanelSetDTO[]> resp = restTemplate.postForEntity(
                webtoonServiceUrl + "/generate_sets", req, PanelSetDTO[].class);

        // 7) Save panel candidates
        for (PanelSetDTO set : resp.getBody()) {
            for (PanelDTO p : set.getPanels()) {
                PanelCandidate cand = new PanelCandidate();
                cand.setSession(session);
                cand.setSetId(set.getSetId());
                cand.setPanelIndex((short)p.getPanelIndex());
                cand.setSeed(p.getSeed());
                cand.setPromptEn(p.getPromptEn());
                cand.setImagePath(p.getImagePath());
                cand.setCreatedAt(LocalDateTime.now());
                candidateRepo.save(cand);
            }
        }

        // 8) Build response
        CreateWebtoonSessionDTO out = new CreateWebtoonSessionDTO();
        out.setSessionId(session.getId());
        out.setSets(Arrays.asList(resp.getBody()));
        return out;

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


