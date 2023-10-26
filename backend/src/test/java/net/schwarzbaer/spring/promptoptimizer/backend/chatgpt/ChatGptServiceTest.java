package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
	void whenAskChatGPT_getsAPrompt_returnsAnAnswer() {
		// Given
		chatGptService = new ChatGptService(
				"ApiKey",
				"OrgKey",
				mockWebServer.url("/").toString()
		);
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
		Answer actual = chatGptService.askChatGPT(new Prompt("TestPrompt"));

		// Then
		Answer expected = new Answer("TestAnswer");
		assertEquals(expected, actual);
	}

	@Test
	void whenAskChatGPT_getsAPrompt_andGivesAWrongResponse() {
		// Given
		chatGptService = new ChatGptService(
				"ApiKey",
				"OrgKey",
				mockWebServer.url("/").toString()
		);
		mockWebServer.enqueue(
				new MockResponse()
						.setHeader("Content-Type", "application/json")
						.setBody("""
								{
								    "choices": [
								        null
								    ],
								    "usage": {
								        "total_tokens": 35
								    }
								}
								""")
		);

		// When
		Answer actual = chatGptService.askChatGPT(new Prompt("TestPrompt"));

		// Then
		assertNull(actual);
	}

	@Test
	void whenGetApiState_isCalledWithDisabledAPI_returnsDisabled() {
		// Given
		chatGptService = new ChatGptService(
				"disabled",
				"disabled",
				mockWebServer.url("/").toString()
		);

		// When
		ApiState actual = chatGptService.getApiState();

		// Then
		ApiState expected = new ApiState(false);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetApiState_isCalledWithEnabledAPI_returnsEnabled() {
		// Given
		chatGptService = new ChatGptService(
				"ApiKey",
				"OrgKey",
				mockWebServer.url("/").toString()
		);

		// When
		ApiState actual = chatGptService.getApiState();

		// Then
		ApiState expected = new ApiState(true);
		assertEquals(expected, actual);
	}
}