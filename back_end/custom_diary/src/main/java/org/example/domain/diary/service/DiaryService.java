package org.example.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.DiaryRequestDTO;
import org.example.domain.diary.dto.DiaryResponseDTO;
import org.example.domain.diary.entity.Diary;
import org.example.domain.diary.entity.DiaryImage;
import org.example.domain.diary.repository.DiaryImageRepository;
import org.example.domain.diary.repository.DiaryRepository;
import org.example.domain.user.entity.User;
import org.example.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepo;
    private final DiaryImageRepository imageRepo;
    private final UserRepository userRepo;

    @Transactional
    public DiaryResponseDTO createDiary(String firebaseUid, DiaryRequestDTO req) {
        User user = userRepo.findById(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));

        Diary diary = new Diary();
        diary.setUser(user);
        diary.setTitle(req.getTitle());
        diary.setContent(req.getContent());
        diary.setBookmark(req.isBookmark());
        diary = diaryRepo.save(diary);

        Diary savedDiary = diaryRepo.save(diary);
        // save images
        req.getImagePaths().forEach(path -> {
            DiaryImage img = new DiaryImage();
            img.setDiary(savedDiary);
            img.setImagePath(path);
            imageRepo.save(img);
        });

        DiaryResponseDTO res = new DiaryResponseDTO();
        res.setId(diary.getId());
        res.setTitle(diary.getTitle());
        res.setContent(diary.getContent());
        res.setBookmark(diary.isBookmark());
        res.setCreatedAt(diary.getCreatedAt());
        res.setImagePaths(
                diary.getImages().stream()
                        .map(DiaryImage::getImagePath)
                        .collect(Collectors.toList())
        );
        return res;
    }

    public List<DiaryResponseDTO> listDiaries(String firebaseUid) {
        return diaryRepo.findAllByUserFirebaseUid(firebaseUid)
                .stream().map(diary -> {
                    DiaryResponseDTO dto = new DiaryResponseDTO();
                    dto.setId(diary.getId());
                    dto.setTitle(diary.getTitle());
                    dto.setContent(diary.getContent());
                    dto.setBookmark(diary.isBookmark());
                    dto.setCreatedAt(diary.getCreatedAt());
                    dto.setImagePaths(
                            diary.getImages().stream()
                                    .map(DiaryImage::getImagePath)
                                    .collect(Collectors.toList())
                    );
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiaryResponseDTO getDiary(String userId, Long diaryId) {
        Diary d = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다"));
        if (!d.getUser().getFirebaseUid().equals(userId)) {
            throw new SecurityException("권한 없음");
        }
        DiaryResponseDTO dto = new DiaryResponseDTO();
        dto.setId(d.getId()); dto.setTitle(d.getTitle()); dto.setContent(d.getContent());
        dto.setBookmark(d.isBookmark()); dto.setCreatedAt(d.getCreatedAt());
        dto.setImagePaths(d.getImages().stream().map(DiaryImage::getImagePath).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public DiaryResponseDTO updateDiary(String userId, Long diaryId, DiaryRequestDTO req) {
        Diary d = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다"));
        if (!d.getUser().getFirebaseUid().equals(userId)) {
            throw new SecurityException("권한 없음");
        }
        d.setTitle(req.getTitle());
        d.setContent(req.getContent());
        d.setBookmark(req.isBookmark());
        // 이미지 교체: 기존 삭제 후 새로 저장
        imageRepo.deleteAll(d.getImages());
        req.getImagePaths().forEach(path -> {
            DiaryImage img = new DiaryImage();
            img.setDiary(d);
            img.setImagePath(path);
            imageRepo.save(img);
        });
        Diary updated = diaryRepo.save(d);
        return toDto(updated);
    }

    @Transactional
    public void deleteDiary(String userId, Long diaryId) {
        Diary d = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다"));
        if (!d.getUser().getFirebaseUid().equals(userId)) {
            throw new SecurityException("권한 없음");
        }
        diaryRepo.delete(d);
    }

    private DiaryResponseDTO toDto(Diary d) {
        DiaryResponseDTO dto = new DiaryResponseDTO();
        dto.setId(d.getId());
        dto.setTitle(d.getTitle());
        dto.setContent(d.getContent());
        dto.setBookmark(d.isBookmark());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setImagePaths(
                d.getImages().stream().map(DiaryImage::getImagePath).collect(Collectors.toList())
        );
        return dto;
    }
}



