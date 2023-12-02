package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

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

import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService.Registration;
import okhttp3.mockwebserver.MockWebServer;

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
	void whenAskChatGPT_withNoUser_returnsStatus401() throws Exception {
		// Given

		// When
		mockMvc
				.perform(ChatGptTestTools.buildAskRequest("TestPrompt"))

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
				.andExpect(content().string(""));
	}

	@Test
	void whenAskChatGPT_withUnknownAccount_returnsStatus403() throws Exception {
		// Given

		// When
		mockMvc
				.perform(ChatGptTestTools.buildAskRequest("TestPrompt", Role.UNKNOWN_ACCOUNT, Registration.GOOGLE))

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
				.andExpect(content().string(""));
	}

	@ParameterizedTest
	@ArgumentsSource(SecurityTestTools.UserAndAdminRoles.class)
	void whenAskChatGPT_withAllowedUser_returnsAnswer(Role role) throws Exception {
		// Given
		mockWebServer.enqueue(
				ChatGptTestTools.buildApiResponse("TestAnswer", 12, 23, 35)
		);

		// When
		mockMvc
				.perform(ChatGptTestTools.buildAskRequest("TestPrompt", role, Registration.GOOGLE))

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