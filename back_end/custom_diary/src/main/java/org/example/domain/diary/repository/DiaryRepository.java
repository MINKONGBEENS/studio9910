package org.example.domain.diary.repository;

import org.example.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByUserFirebaseUid(String uid);
    List<Diary> findAllByUserFirebaseUidAndCreatedAtBetween(String uid, LocalDateTime start, LocalDateTime end);
}
