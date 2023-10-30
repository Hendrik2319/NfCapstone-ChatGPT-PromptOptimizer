package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChatGptServiceTest {

	private static MockWebServer mockWebServer;
	private ChatGptService chatGptService;

	@BeforeAll
	static void setup() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterAll
	static void teardown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void whenAskChatGPT_isCalledWithDisabledAPI_returnsAnGeneratedAnswer() {
		// Given
		chatGptService = new ChatGptService(
				"disabled",
				"disabled",
				mockWebServer.url("/").toString()
		);

		// When
		Answer actual = chatGptService.askChatGPT(new Prompt("TestPrompt"));

		// Then
		Answer expected = new Answer("Access to OpenAI API is currently disabled.%nYour prompt was:%n\"%s\"".formatted("TestPrompt"));
		assertEquals(expected, actual);
	}

	@Test
	void whenAskChatGPT_getsAPrompt1_returnsAnAnswer() {
		whenAskChatGPT_getsAPrompt_returnsAnAnswer(
				"""
					{
					    "choices": [ { "message": { "content": "%s" } } ],
					    "usage": { "prompt_tokens": 12, "completion_tokens": 23, "total_tokens": 35 }
					}
				""",
				12,23,35
		);
	}
	@Test
	void whenAskChatGPT_getsAPrompt2_returnsAnAnswer() {
		whenAskChatGPT_getsAPrompt_returnsAnAnswer(
				"""
					{
					    "choices": [ { "message": { "content": "%s" } } ]
					}
				""",
				0,0,0
		);
	}
	private void whenAskChatGPT_getsAPrompt_returnsAnAnswer(String body, int promptTokens, int completionTokens, int totalTokens) {
		// Given
		chatGptService = new ChatGptService(
				"ApiKey",
				"OrgKey",
				mockWebServer.url("/").toString()
		);
		mockWebServer.enqueue(
				new MockResponse()
						.setHeader("Content-Type", "application/json")
						.setBody(body.formatted("TestAnswer"))
		);

		// When
		Answer actual = chatGptService.askChatGPT(new Prompt("TestPrompt"));

		// Then
		Answer expected = new Answer("TestAnswer", promptTokens, completionTokens, totalTokens);
		assertEquals(expected, actual);
	}

	@ParameterizedTest
	@ValueSource( strings = {
		"{ \"choices\": [ { \"message\": {} } ], \"usage\": { \"total_tokens\": 35 } }",
		"{ \"choices\": [ {} ], \"usage\": { \"total_tokens\": 35 } }",
		"{ \"choices\": [ null ], \"usage\": { \"total_tokens\": 35 } }",
		"{ \"choices\": [], \"usage\": { \"total_tokens\": 35 } }",
		"{ \"usage\": { \"total_tokens\": 35 } }",
	} )
	void whenAskChatGPT_getsAPrompt_andApiGivesAWrongResponse_returnsNull(String wrongBody) {
		whenAskChatGPT_andSomethingWentWrong_returnsNull(
				new MockResponse()
						.setHeader("Content-Type", "application/json")
						.setBody(wrongBody)
		);
	}

	@ParameterizedTest
	@ValueSource( ints = { 400, 401, 403, 500, 501, 502 ,503 } )
	void whenAskChatGPT_getsAPrompt_andApiGivesStatusXXX_returnsNull(int responseCode) {
		whenAskChatGPT_andSomethingWentWrong_returnsNull(
				new MockResponse()
						.setResponseCode(responseCode)
		);
	}

	private void whenAskChatGPT_andSomethingWentWrong_returnsNull(MockResponse mockResponse) {
		// Given
		chatGptService = new ChatGptService(
				"ApiKey",
				"OrgKey",
				mockWebServer.url("/").toString()
		);
		mockWebServer.enqueue(
				mockResponse
		);

		// When
		Answer actual = chatGptService.askChatGPT(new Prompt("TestPrompt"));

		// Then
		assertNull(actual);
	}

	@Test void whenGetApiState_isCalledWithDisabledAPI1_returnsDisabled() {
		whenGetApiState_isCalled("disabled", "disabled", false);
	}
	@Test void whenGetApiState_isCalledWithDisabledAPI2_returnsDisabled() {
		whenGetApiState_isCalled("disabled_", "disabled", false);
	}
	@Test void whenGetApiState_isCalledWithDisabledAPI3_returnsDisabled() {
		whenGetApiState_isCalled("disabled", "disabled_", false);
	}
	@Test void whenGetApiState_isCalledWithEnabledAPI_returnsEnabled() {
		whenGetApiState_isCalled("ApiKey", "OrgKey", true);
	}

	private void whenGetApiState_isCalled(String openaiApiKey, String openaiApiOrganization, boolean isApiEnabled) {
		// Given
		chatGptService = new ChatGptService(
				openaiApiKey,
				openaiApiOrganization,
				mockWebServer.url("/").toString()
		);

		// When
		ApiState actual = chatGptService.getApiState();

		// Then
		ApiState expected = new ApiState(isApiEnabled);
		assertEquals(expected, actual);
	}
}