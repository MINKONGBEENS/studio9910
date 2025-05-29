package org.example.domain.diary.service;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.domain.diary.dto.StructuredPrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptExtractionService {
    private final OpenAiService openAi;
    private final ObjectMapper objectMapper;

    public PromptExtractionService(@Value("${openai.api.key}") String apiKey, ObjectMapper mapper) {
        this.openAi = new OpenAiService(apiKey);
        this.objectMapper = mapper;
    }

    /**
     * 구조화된 프롬프트 추출: char_info(부분), background, caption
     * * GPT가 객체의 JSON 배열을 반환할 것으로 예상합니다.
     */
    public List<StructuredPrompt> extractStructuredPrompts(String text, int count) {
        ChatMessage system = new ChatMessage("system",
                "You are a JSON generator. From the following diary entry, extract exactly "
                        + count
                        + " prompts. Each prompt must be an object with keys:\n"
                        + "  • char_info: { gender:'', importance:'', age:'', kind:'', shape:'', movement:'', clothing:'' }\n"
                        + "  • background: { background_info:'' }\n"
                        + "  • caption: ''\n"
                        + "Return a JSON array of these objects, e.g.:\n"
                        + "[\n"
                        + "  {\"char_info\":{\"gender\":\"M\",\"importance\":\"main\",\"age\":\"25\","
                        + "\"kind\":\"brave\",\"shape\":\"slim\",\"movement\":\"running\",\"clothing\":\"jacket\"},\n"
                        + "   \"background\":{\"background_info\":\"park at dusk\"},\n"
                        + "   \"caption\":\"A hero sprints through the park at dusk.\"\n"
                        + "  },\n"
                        + "  …\n]"
        );
        ChatMessage user = new ChatMessage("user",
                "Diary entry:\n\"\"\"\n" + text + "\n\"\"\"");
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Arrays.asList(system, user))
                .maxTokens(512)
                .temperature(0.7)
                .build();

        ChatCompletionResult result = openAi.createChatCompletion(request);
        String json = result.getChoices().get(0).getMessage().getContent().trim();

        try {
            List<StructuredPrompt> prompts = objectMapper.readValue(
                    json,
                    new TypeReference<List<StructuredPrompt>>() {}
            );
            return prompts.stream().limit(count).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse structured prompts: " + json, e);
        }
    }

}