package org.example.domain.gallery.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookmarkDiaryDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String mergedWebtoonUrl;  // weekly_webtoons 테이블에서 조회된 URL (있으면)
}