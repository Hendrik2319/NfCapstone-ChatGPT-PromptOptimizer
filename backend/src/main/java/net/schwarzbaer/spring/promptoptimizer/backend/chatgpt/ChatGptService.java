package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.PrintStream;
import java.util.List;

@Slf4j
@Service
public class ChatGptService {

	private final WebClient webClient;
	@SuppressWarnings("java:S106")
	private static final PrintStream DEBUG_OUT = System.out;

	public ChatGptService(
			@Value("${app.openai-api-key}") String openaiApiKey,
			@Value("${app.openai-api-org}") String openaiApiOrganization,
			@Value("${app.openai-api-url}") String baseUrl
	) {
		if ("disabled".equals(openaiApiKey) || "disabled".equals(openaiApiOrganization))
			this.webClient = null;
		else
			this.webClient = WebClient.builder()
					.baseUrl(baseUrl)
					.defaultHeader("Authorization", "Bearer " + openaiApiKey)
					.defaultHeader("OpenAI-Organization", openaiApiOrganization)
					.build();
	}

	public Answer askChatGPT(@NonNull Prompt prompt) {
		if (webClient == null) // API access is disabled
			return new Answer("Access to OpenAI API is currently disabled.%nYour prompt was:%n\"%s\"".formatted(prompt.prompt()));

		ChatGptRequest request = new ChatGptRequest(
				"gpt-3.5-turbo",
				List.of(
						new ChatGptRequest.Message(
								"user",
								prompt.prompt()
						)
				)
		);

//		request.showContent(DEBUG_OUT, "request");
		log.info("##### ChatGpt.Request: %s".formatted(request));

		ChatGptResponse response = execRequest(request);
		log.info("##### ChatGpt.Response: %s".formatted(request));
		if (response == null) {/*DEBUG_OUT.println("response: <null>");*/ return null; }

//		response.showContent(DEBUG_OUT, "response");

		List<ChatGptResponse.Choice> choices = response.choices();
		if (choices == null || choices.isEmpty()) return null;

		ChatGptResponse.Choice firstChoice = choices.get(0);
		if (firstChoice == null) return null;

		ChatGptResponse.Message message = firstChoice.message();
		if (message == null) return null;

		String content = message.content();
		if (content == null) return null;

		ChatGptResponse.Usage usage = response.usage();
		if (usage == null)
			return new Answer(content);

		return new Answer(
				content,
				usage.prompt_tokens(),
				usage.completion_tokens(),
				usage.total_tokens()
		);
	}

	private ChatGptResponse execRequest(ChatGptRequest request) {
		log.info("##### ChatGpt.execRequest: START -> make POST request");
		WebClient.ResponseSpec responseSpec = webClient.post()
				.bodyValue(request)
				.retrieve();
		log.info("##### ChatGpt.execRequest: got ResponseSpec");

		WebClient.ResponseSpec responseSpec1 = responseSpec
				.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
				.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty());
		log.info("##### ChatGpt.execRequest: error status codes (4xx, 5xx) checked");

		Mono<ResponseEntity<ChatGptResponse>> mono = responseSpec1
				.toEntity(ChatGptResponse.class);
		log.info("##### ChatGpt.execRequest: got Mono: %s".formatted(mono));

		ResponseEntity<ChatGptResponse> responseEntity = mono
				.block();
		log.info("##### ChatGpt.execRequest got responseEntity -> END");

		if (responseEntity == null) return null;

		HttpStatusCode statusCode = responseEntity.getStatusCode();
		if (statusCode.is4xxClientError()) return null;
		if (statusCode.is5xxServerError()) return null;

		return responseEntity.getBody();
	}

	public @NonNull ApiState getApiState() {
		return new ApiState(webClient != null);
	}
}
