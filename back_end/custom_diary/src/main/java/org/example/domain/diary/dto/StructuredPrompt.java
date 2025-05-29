package org.example.domain.diary.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StructuredPrompt {
    private Map<String, String> charInfo;
    private Map<String, String> background;
    private String caption;
}
