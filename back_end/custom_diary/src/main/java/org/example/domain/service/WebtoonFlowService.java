package org.example.domain.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.*;
import org.example.domain.diary.entity.*;
import org.example.domain.diary.repository.*;
import org.example.domain.diary.dto.PanelSetDTO;
import org.example.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${webtoon.service.url}")
    private String webtoonServiceUrl;

    @Transactional
    public CreateWebtoonSessionDTO createSession(String userId, Long diaryId, int panelsCount) {
        // 1) 일기 내용에서 나온 더미 프롬프트
        List<String> prompts = Arrays.asList("p1","p2","p3","p4","p5");
        Collections.shuffle(prompts);
        List<String> selected = prompts.stream().limit(panelsCount).collect(Collectors.toList());

        // 2) 세션 유지
        GenerationSession session = new GenerationSession();
        session.setId(UUID.randomUUID());
        session.setUser(userRepo.getReferenceById(userId));
        session.setDiary(diaryRepo.getReferenceById(diaryId));
        session.setCreatedAt(LocalDateTime.now());
        sessionRepo.save(session);

        // 3) 마이크로서비스 호출
        HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> body = new HashMap<>();
        body.put("session_id", session.getId().toString());
        List<Map<String,Object>> panels = selected.stream().map(c -> Map.of(
                "char_info", Collections.emptyMap(),
                "background", Collections.emptyMap(),
                "caption", c
        )).collect(Collectors.toList());
        body.put("panels", panels);
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, headers);
        ResponseEntity<PanelSetDTO[]> resp = restTemplate.postForEntity(
                webtoonServiceUrl + "/generate_sets", req, PanelSetDTO[].class);

        // 4) candidates 저장
        for (PanelSetDTO set: resp.getBody()) for (PanelDTO p: set.getPanels()) {
            PanelCandidate cand = new PanelCandidate();
            cand.setSession(session); cand.setSetId(set.getSetId());
            cand.setPanelIndex((short)p.getPanelIndex()); cand.setSeed(p.getSeed());
            cand.setPromptEn(""); cand.setImagePath(p.getImagePath());
            cand.setCreatedAt(LocalDateTime.now()); candidateRepo.save(cand);
        }

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

    @Transactional(readOnly=true)
    public WeeklyWebtoonResponseDTO generateWeekly(String userId, int year, int month, int week) {
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        LocalDate start = firstOfMonth.plusWeeks(week-1);
        LocalDate end = start.plusWeeks(1);
        LocalDateTime fromLdt = start.atStartOfDay();
        LocalDateTime toLdt = end.atStartOfDay();
        // gather diaries
        List<Diary> list = diaryRepo.findAllByUserFirebaseUidAndCreatedAtBetween(userId, fromLdt, toLdt);
        // 프롬프트 수집
        List<String> prompts = list.stream()
                .flatMap(d->Arrays.stream(d.getContent().split("[.?!]\s*")))
                .collect(Collectors.toList());
        Collections.shuffle(prompts);
        List<String> sel = prompts.stream().limit(4).collect(Collectors.toList());
        // 세션 및 패널 생성
        CreateWebtoonSessionDTO cs = createSession(userId, list.get(0).getId(), 4);
        // 첫 번째 세트를 자동으로 선택하고 병합
        Map<String,Object> body = Map.of("diary_id", cs.getSessionId().toString());
        String merged = restTemplate.postForObject(
                webtoonServiceUrl + "/merge_panels", body, Map.class).get("merged_image_url").toString();
        WeeklyWebtoonResponseDTO out = new WeeklyWebtoonResponseDTO();
        out.setSessionId(cs.getSessionId()); out.setMergedImageUrl(merged);
        return out;
    }
}

