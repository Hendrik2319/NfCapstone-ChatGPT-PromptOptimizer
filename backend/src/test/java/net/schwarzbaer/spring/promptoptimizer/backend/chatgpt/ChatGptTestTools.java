package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService.Registration;
import okhttp3.mockwebserver.MockResponse;

public class ChatGptTestTools {

	public static MockHttpServletRequestBuilder buildAskRequest(String prompt, Role role, @NonNull Registration registration) {
		return MockMvcRequestBuilders
				.post("/api/ask")

				.with(SecurityTestTools.buildUser(role, registration))

				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"prompt": "%s"
						}
						""".formatted(prompt));
	}

	public static MockHttpServletRequestBuilder buildAskRequest(String prompt) {
		return MockMvcRequestBuilders
				.post("/api/ask")

				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"prompt": "%s"
						}
						""".formatted(prompt));
	}

	public static MockResponse buildApiResponse(String answer, int prompt_tokens, int completion_tokens, int total_tokens) {
		return new MockResponse()
				.setHeader("Content-Type", "application/json")
				.setBody("""
						{
							"choices": [
								{
									"message": {
										"content": "%s"
									}
								}
							],
							"usage": {
								"prompt_tokens": %d,
								"completion_tokens": %d,
								"total_tokens": %d
							}
						}
						"""
						.formatted(
								answer,
								prompt_tokens,
								completion_tokens,
								total_tokens
						));
	}

}
