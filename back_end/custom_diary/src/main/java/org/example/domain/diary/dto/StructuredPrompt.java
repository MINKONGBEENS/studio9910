package org.example.domain.diary.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StructuredPrompt {
    private Map<String, String> charInfo;
    private Map<String, Object> background;
    private String caption;
}
