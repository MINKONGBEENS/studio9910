package org.example.domain.diary.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryResponseDTO {
    private Long id;
    private String title;
    private String content;
    private boolean bookmark;
    private LocalDateTime createdAt;
    private List<String> imagePaths;
}
