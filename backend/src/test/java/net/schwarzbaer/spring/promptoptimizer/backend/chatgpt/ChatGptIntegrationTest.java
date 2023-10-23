package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatGptIntegrationTest {

	@MockBean
	private ClientRegistrationRepository clientRegistrationRepository;

	private static MockWebServer mockWebServer;

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	static void setup() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterAll
	static void teardown() throws IOException {
		mockWebServer.shutdown();
	}

	@DynamicPropertySource
	static void setUrlDynamically(DynamicPropertyRegistry reg) {
		reg.add("app.openai-api-key", () -> "dummy_api_key");
		reg.add("app.openai-api-org", () -> "dummy_api_org");
		reg.add("app.openai-api-url", () -> mockWebServer.url("/").toString());
	}

	@Test
	void whenAskChatGPT_getsAPrompt_returnsAnAnswer() throws Exception {
		// Given
		mockWebServer.enqueue(
				new MockResponse()
						.setHeader("Content-Type", "application/json")
						.setBody("""
								{
									"choices": [
										{
											"message": {
												"content": "TestAnswer"
											}
										}
									],
									"usage": {
										"total_tokens": 35
									}
								}
								""")
		);

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/ask")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"prompt": "TestPrompt"
								}
								""")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
								{
									"answer": "TestAnswer"
								}
								"""));
	}

	@Test
	void whenGetApiState_isCalledWithEnabledAPI_returnsEnabled() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/apistate")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
								{
									"enabled": true
								}
								"""));

	}
}