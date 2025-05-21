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
}



