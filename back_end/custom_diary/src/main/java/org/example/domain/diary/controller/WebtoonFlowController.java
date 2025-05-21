package org.example.domain.diary.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.*;

import org.example.domain.diary.service.WebtoonFlowService;
import org.example.domain.gallery.dto.WeeklyFolderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class WebtoonFlowController {
    private final WebtoonFlowService flowService;

    @PostMapping("/{diaryId}/webtoon-session")
    public ResponseEntity<CreateWebtoonSessionDTO> create(
            Authentication auth,
            @PathVariable Long diaryId,
            @RequestParam(defaultValue="4") int panels) {
        String uid = auth.getName();
        return ResponseEntity.ok(flowService.createSession(uid, diaryId, panels));
    }

    @PostMapping("/{diaryId}/webtoon-selection")
    public ResponseEntity<Void> select(
            Authentication auth,
            @RequestBody SelectWebtoonDTO dto) {
        flowService.selectWebtoon(dto, auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyFolderDTO> weekly(
            Authentication auth,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int week) {
        String uid = auth.getName();
        return ResponseEntity.ok(flowService.generateWeekly(uid, year, month, week));
    }
}

