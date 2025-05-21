package org.example.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.diary.entity.WeeklyWebtoon;
import org.example.domain.diary.repository.WeeklyWebtoonRepository;
import org.example.domain.gallery.dto.*;
import org.example.domain.gallery.entity.Folder;
import org.example.domain.gallery.repository.FolderRepository;
import org.example.domain.diary.entity.Diary;
import org.example.domain.diary.repository.DiaryRepository;
import org.example.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepo;
    private final DiaryRepository diaryRepo;
    private final WeeklyWebtoonRepository weeklyRepo;

    // **신규**: 가입 직후 기본 폴더 2개 생성
    @Transactional
    public void initializeDefaultFolders(String userId) {
        // 북마크용
        FolderRequestDTO bookmark = new FolderRequestDTO();
        bookmark.setName("Bookmark");
        createFolder(userId, bookmark);

        // 주간 웹툰용
        FolderRequestDTO weekly = new FolderRequestDTO();
        weekly.setName("Weekly Webtoon");
        createFolder(userId, weekly);
    }

    @Transactional
    public FolderResponseDTO createFolder(String userId, FolderRequestDTO req) {
        Folder f = new Folder();
        f.setUser(new User());
        f.getUser().setFirebaseUid(userId);
        f.setName(req.getName());
        f.setCreatedAt(LocalDateTime.now());
        f = folderRepo.save(f);

        FolderResponseDTO res = new FolderResponseDTO();
        res.setId(f.getId());
        res.setName(f.getName());
        res.setCreatedAt(f.getCreatedAt());
        res.setDiaryIds(List.of());
        return res;
    }

    @Transactional(readOnly = true)
    public List<FolderResponseDTO> listFolders(String userId) {
        return folderRepo.findAllByUserFirebaseUid(userId).stream().map(f -> {
            FolderResponseDTO dto = new FolderResponseDTO();
            dto.setId(f.getId());
            dto.setName(f.getName());
            dto.setCreatedAt(f.getCreatedAt());
            dto.setDiaryIds(
                    f.getDiaries().stream()
                            .map(Diary::getId)
                            .collect(Collectors.toList())
            );
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addDiaryToFolder(String userId, Long folderId, AddDiaryToFolderDTO req) {
        Folder f = folderRepo.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾지 못했습니다"));
        if (!f.getUser().getFirebaseUid().equals(userId))
            throw new SecurityException("권한이 없습니다");
        Diary d = diaryRepo.findById(req.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾지 못했습니다"));
        f.getDiaries().add(d);
        folderRepo.save(f);
    }

    @Transactional
    public void removeDiaryFromFolder(String userId, Long folderId, Long diaryId) {
        Folder f = folderRepo.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾지 못했습니다"));
        if (!f.getUser().getFirebaseUid().equals(userId))
            throw new SecurityException("권한이 없습니다");
        f.getDiaries().removeIf(d -> d.getId().equals(diaryId));
        folderRepo.save(f);
    }

    @Transactional(readOnly = true)
    public FolderResponseDTO getFolder(String userId, Long folderId) {
        Folder f = folderRepo.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾지 못했습니다"));
        if (!f.getUser().getFirebaseUid().equals(userId))
            throw new SecurityException("권한이 없습니다");
        FolderResponseDTO dto = new FolderResponseDTO();
        dto.setId(f.getId()); dto.setName(f.getName());
        dto.setCreatedAt(f.getCreatedAt());
        dto.setDiaryIds(
                f.getDiaries().stream()
                        .map(Diary::getId)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    /** 북마크 폴더 **/
    @Transactional(readOnly = true)
    public List<BookmarkDiaryDTO> listBookmarkedDiaries(String userId) {
        return diaryRepo.findAllByUserFirebaseUid(userId)
                .stream()
                .filter(Diary::isBookmark)
                .map(d -> {
                    BookmarkDiaryDTO dto = new BookmarkDiaryDTO();
                    dto.setId(d.getId());
                    dto.setTitle(d.getTitle());
                    dto.setContent(d.getContent());
                    dto.setCreatedAt(d.getCreatedAt());
                    // optional: 해당 일기가 속한 주의 merged_webtoon URL 연결
                    // 선택된 세션의 병합 웹툰 URL이 있다면 조회
                    WeeklyWebtoon w = weeklyRepo
                            .findAllByUserIdAndYearAndMonthAndWeek(
                                    userId,
                                    d.getCreatedAt().atZone(ZoneOffset.systemDefault()).getYear(),
                                    d.getCreatedAt().getMonthValue(),
                                    (d.getCreatedAt().getDayOfMonth()-1)/7 + 1
                            ).stream().findFirst().orElse(null);
                    dto.setMergedWebtoonUrl(w!=null? w.getMergedImageUrl(): null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /** 주간 웹툰 폴더 **/
    @Transactional(readOnly = true)
    public List<WeeklyFolderDTO> listWeeklyWebtoons(String userId) {
        return weeklyRepo.findAllByUserIdOrderByYearDescMonthDescWeekDesc(userId)
                .stream()
                .map(w -> {
                    WeeklyFolderDTO dto = new WeeklyFolderDTO();
                    dto.setYear(w.getYear());
                    dto.setMonth(w.getMonth());
                    dto.setWeek(w.getWeek());
                    dto.setMergedImageUrl(w.getMergedImageUrl());
                    dto.setSessionId(w.getSessionId());
                    dto.setCreatedAt(w.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}