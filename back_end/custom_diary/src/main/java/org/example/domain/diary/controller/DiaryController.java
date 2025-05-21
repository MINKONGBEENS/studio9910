package org.example.domain.diary.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.DiaryRequestDTO;
import org.example.domain.diary.dto.DiaryResponseDTO;
import org.example.domain.diary.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryResponseDTO> createDiary(
            Authentication authentication,
            @Valid @RequestBody DiaryRequestDTO req) {
        String uid = authentication.getName();
        DiaryResponseDTO res = diaryService.createDiary(uid, req);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<List<DiaryResponseDTO>> listDiaries(
            Authentication authentication) {
        String uid = authentication.getName();
        List<DiaryResponseDTO> list = diaryService.listDiaries(uid);
        return ResponseEntity.ok(list);
    }

    // 단일 일기 호출
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryResponseDTO> getDiary(Authentication auth, @PathVariable Long diaryId) {
        return ResponseEntity.ok(diaryService.getDiary(auth.getName(), diaryId));
    }

    @PutMapping("/{diaryId}")
    public ResponseEntity<DiaryResponseDTO> updateDiary(
            Authentication auth,
            @PathVariable Long diaryId,
            @Valid @RequestBody DiaryRequestDTO req) {
        String uid = auth.getName();
        return ResponseEntity.ok(
                diaryService.updateDiary(uid, diaryId, req)
        );
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            Authentication auth,
            @PathVariable Long diaryId) {
        diaryService.deleteDiary(auth.getName(), diaryId);
        return ResponseEntity.noContent().build();
    }
}

