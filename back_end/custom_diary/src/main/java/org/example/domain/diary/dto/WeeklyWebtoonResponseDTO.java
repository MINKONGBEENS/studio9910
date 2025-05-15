package org.example.domain.diary.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WeeklyWebtoonResponseDTO {
    private String mergedImageUrl;
    private UUID sessionId;
}
