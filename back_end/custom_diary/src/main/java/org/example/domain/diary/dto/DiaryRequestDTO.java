package org.example.domain.diary.dto;

import lombok.Data;
import java.util.List;

@Data
public class DiaryRequestDTO {
    private String title;
    private String content;
    private List<String> imagePaths; // Firebase URLs
    private boolean bookmark;
}
