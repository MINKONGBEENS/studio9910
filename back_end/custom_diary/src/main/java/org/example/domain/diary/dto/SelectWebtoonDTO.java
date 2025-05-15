package org.example.domain.diary.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SelectWebtoonDTO {
    private UUID sessionId;
    private short selectedSet;
}
