package org.example.domain.diary.dto;

import lombok.Data;

@Data
public class PanelDTO {
    private int panelIndex;
    private long seed;
    private String promptEn;
    private String imagePath;
}
