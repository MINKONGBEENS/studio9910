package org.example.domain.gallery.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WeeklyFolderDTO {
    private int year;
    private int month;
    private int week;
    private String mergedImageUrl;
    private UUID sessionId;
    private LocalDateTime createdAt;
}
