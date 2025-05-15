package org.example.domain.diary.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PromptRequestDTO {
    List<String> charInfo;
    Map<String,Object> background;
    String caption; }
