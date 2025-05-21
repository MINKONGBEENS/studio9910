package org.example.domain.gallery.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.gallery.dto.*;
import org.example.domain.gallery.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<FolderResponseDTO> create(
            Authentication auth,
            @RequestBody FolderRequestDTO req) {
        String uid = auth.getName();
        return ResponseEntity.ok(
                folderService.createFolder(uid, req)
        );
    }

    @GetMapping
    public ResponseEntity<List<FolderResponseDTO>> list(
            Authentication auth) {
        String uid = auth.getName();
        return ResponseEntity.ok(
                folderService.listFolders(uid)
        );
    }

    @PostMapping("/{folderId}/diaries")
    public ResponseEntity<Void> addDiary(
            Authentication auth,
            @PathVariable Long folderId,
            @RequestBody AddDiaryToFolderDTO req) {
        folderService.addDiaryToFolder(auth.getName(), folderId, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folderId}/diaries/{diaryId}")
    public ResponseEntity<Void> removeDiary(
            Authentication auth,
            @PathVariable Long folderId,
            @PathVariable Long diaryId) {
        folderService.removeDiaryFromFolder(auth.getName(), folderId, diaryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<FolderResponseDTO> getFolder(
            Authentication auth,
            @PathVariable Long folderId) {
        return ResponseEntity.ok(
                folderService.getFolder(auth.getName(), folderId)
        );
    }

    @GetMapping("/bookmark")
    public ResponseEntity<List<BookmarkDiaryDTO>> bookmark(
            Authentication auth) {
        String uid = auth.getName();
        return ResponseEntity.ok(folderService.listBookmarkedDiaries(uid));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyFolderDTO>> weekly(
            Authentication auth) {
        String uid = auth.getName();
        return ResponseEntity.ok(folderService.listWeeklyWebtoons(uid));
    }
}
