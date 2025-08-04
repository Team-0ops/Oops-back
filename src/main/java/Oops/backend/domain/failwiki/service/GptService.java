package Oops.backend.domain.failwiki.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String summarizePosts(List<String> contents, String keyword, boolean isBulk) {
        String prompt;
        if (isBulk) {
            prompt = """
            다음은 키워드 '%s'와 관련된 실패 극복 방법입니다.
            총 %d개의 글에서 추출된 내용입니다.
            중복 내용을 제거하고 500자 내로 핵심만 요약해주세요.
            내용:
            %s
            """.formatted(keyword, contents.size(), String.join("\n", contents));
        } else {
            prompt = """
            다음은 키워드 '%s'에 대한 개별 극복 방법입니다.
            각 내용을 80~100자 내로 요약해주세요.
            내용:
            %s
            """.formatted(keyword, String.join("\n", contents));
        }
        return requestGpt(prompt);
    }

    public String aiOneLineTip(String keyword) {
        String prompt = "주제 '" + keyword + "'에 대해 솔루션이 부족할 수 있으니, 전문가 관점에서 100자 이내로 한마디 조언해주세요.";
        return requestGpt(prompt);
    }

    private String requestGpt(String prompt) {
        WebClient client = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 실패 극복 전문가입니다."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        return client.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> ((Map<String, Object>) ((List<?>) resp.get("choices")).get(0))
                        .get("message"))
                .map(msg -> (String) ((Map<?, ?>) msg).get("content"))
                .block();
    }
}