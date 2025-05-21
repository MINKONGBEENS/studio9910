package org.example.domain.gallery.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FolderResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<Long> diaryIds;
}
