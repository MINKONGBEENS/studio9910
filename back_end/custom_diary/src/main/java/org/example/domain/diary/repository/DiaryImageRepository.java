package org.example.domain.diary.repository;

import org.example.domain.diary.entity.DiaryImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryImageRepository extends JpaRepository<DiaryImage, Long> {
}
