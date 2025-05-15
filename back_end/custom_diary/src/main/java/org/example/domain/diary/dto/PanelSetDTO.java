package org.example.domain.diary.dto;

import lombok.Data;

import java.util.List;

@Data
public class PanelSetDTO {
    private short setId;
    private List<PanelDTO> panels;
}
