package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import net.schwarzbaer.spring.promptoptimizer.backend.security.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
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
class ChatGptDisabledApiIntegrationTest {

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
		reg.add("app.openai-api-key", () -> "disabled");
		reg.add("app.openai-api-org", () -> "disabled");
		reg.add("app.openai-api-url", () -> mockWebServer.url("/").toString());
	}

	@Test
	void whenAskChatGPT_withDisabledAPI_withNoUser_returnsStatus401() throws Exception {
		// Given

		// When
		mockMvc
				.perform(ChatGptTestTools.buildAskRequest("TestPrompt"))

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
				.andExpect(content().string(""));
	}

	@Test
	void whenAskChatGPT_withDisabledAPI_withUnknownAccount_returnsStatus403() throws Exception {
		// Given

		// When
		mockMvc
				.perform(ChatGptTestTools.buildAskRequest("TestPrompt", Role.UNKNOWN_ACCOUNT))

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
				.andExpect(content().string(""));
	}

	@ParameterizedTest
	@ArgumentsSource(SecurityTestTools.AllowedUserRoles.class)
	void whenAskChatGPT_withDisabledAPI_withAllowedUser_returnsAnGeneratedAnswer(Role role) throws Exception {
		// Given

		// When
		mockMvc
				.perform(ChatGptTestTools.buildAskRequest("TestPrompt", role))

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{
							"answer": "Access to OpenAI API is currently disabled.\\nYour prompt was:\\n\\"%s\\""
						}
						""".formatted("TestPrompt")));
	}

	@Test
	void whenGetApiState_isCalledWithDisabledAPI_returnsDisabled() throws Exception {
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
									"enabled": false
								}
								"""));

	}
}
