package org.example.domain.diary.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateWebtoonSessionDTO {
    private UUID sessionId;
    private List<PanelSetDTO> sets;
}
