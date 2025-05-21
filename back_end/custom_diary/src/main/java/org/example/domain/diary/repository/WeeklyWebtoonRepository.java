package org.example.domain.diary.repository;

import org.example.domain.diary.entity.WeeklyWebtoon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeeklyWebtoonRepository extends JpaRepository<WeeklyWebtoon, Long> {
    List<WeeklyWebtoon> findAllByUserIdOrderByYearDescMonthDescWeekDesc(String userId);
    List<WeeklyWebtoon> findAllByUserIdAndYearAndMonthAndWeek(
            String userId, int year, int month, int week);
}