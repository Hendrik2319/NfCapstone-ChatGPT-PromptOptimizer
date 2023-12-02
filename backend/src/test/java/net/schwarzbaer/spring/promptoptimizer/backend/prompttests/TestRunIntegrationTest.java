package net.schwarzbaer.spring.promptoptimizer.backend.prompttests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.ChatGptTestTools;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.RunningTestRunsList;
import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService.Registration;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@AutoConfigureMockMvc
class TestRunIntegrationTest {

	@MockBean ClientRegistrationRepository clientRegistrationRepository;

	@Autowired private MockMvc mockMvc;
	@Autowired private ScenarioRepository scenarioRepository;
	@Autowired private TestRunRepository testRunRepository;
	@Autowired private RunningTestRunsList runningTestRunsList;

	private static MockWebServer mockWebServer;

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

	@NonNull
	private static TestRun createTestRun(String testRunId, String scenarioId) {
		return new TestRun(
				testRunId, scenarioId,
				ZonedDateTime.of(2023, 10, 29, 14, 30, 0, 0, ZoneId.systemDefault()),
				"prompt", List.of("var1", "var2"),
				List.of(Map.of("var1", List.of("value1"), "var2", List.of("value2"))),
				List.of(new TestRun.TestAnswer(1, "label", "answer",12,23,35)),
				35.0
		);
	}

	private String createTestRunJSON(@Nullable String testRunId, @NonNull String scenarioId, boolean withTimeStamp) {
		return """
			{
				%s
			    "scenarioId": "%s",
			    %s
			    "prompt"    : "prompt",
			    "variables" : ["var1","var2"],
			    "testcases" : [{"var1":["value1"],"var2":["value2"]}],
			    "answers"   : [{"indexOfTestCase":1,"label":"label","answer":"answer","promptTokens":12,"completionTokens":23,"totalTokens":35}],
			    "averageTokensPerRequest" : 35
			}
		""".formatted(
				testRunId==null ? "" : "\"id\": \"%s\",".formatted(testRunId),
				scenarioId,
				!withTimeStamp ? "" : "\"timestamp\": \"%s\",".formatted(
						ZonedDateTime.of(2023, 10, 29, 14, 30, 0, 0, ZoneId.systemDefault())
				)
		);
	}

	private String createNewTestRunJSON(@Nullable String scenarioId) {
		return createNewTestRunJSON(scenarioId, "DefaultPrompt");
	}
	private String createNewTestRunJSON(@Nullable String scenarioId, @NonNull String prompt) {
		return """
			{
				%s
			    "prompt"    : "%s",
			    "variables" : ["var1","var2"],
			    "testcases" : [{"var1":["value1"],"var2":["value2"]}]
			}
		""".formatted(
				scenarioId==null ? "" : "\"scenarioId\": \"%s\",".formatted(scenarioId),
				prompt
		);
	}

	private static @NonNull String getId(@NonNull String userId, @NonNull Registration registration) {
		return SecurityTestTools.getUserDbId(userId, registration);
	}

//	####################################################################################
//				getTestRunsOfScenario
//	#################################################################################### 

	@Test
	@DirtiesContext
	void whenGetTestRunsOfScenario_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id1/testrun")
				)

				// Then
				.andExpect(status().isUnauthorized())
				.andExpect(content().string(""));
	}

	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenGetTestRunsOfScenario_isCalled_returnsStatus403Forbidden(
			getId("author1", Registration.GOOGLE), Role.UNKNOWN_ACCOUNT, "author1", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledDifferentAuthorIds_returnsStatus403Forbidden() throws Exception {
		whenGetTestRunsOfScenario_isCalled_returnsStatus403Forbidden(
			getId("author3", Registration.GOOGLE), Role.USER, "author2", Registration.GOOGLE
		);
	}
	private void whenGetTestRunsOfScenario_isCalled_returnsStatus403Forbidden(
			@NonNull String storedAuthorID, @NonNull Role role, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", storedAuthorID, "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/%s/testrun".formatted("scenarioId1"))
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
				)

				// Then
				.andExpect(status().isForbidden());
	}

	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledByAdmin_returnsList() throws Exception {
		whenGetTestRunsOfScenario_isCalledByAllowedUser_returnsList(
			getId("authorOther", Registration.GITHUB), Role.ADMIN, "authorAdmin", Registration.GITHUB
		);
	}
	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledByUser_returnsList() throws Exception {
		whenGetTestRunsOfScenario_isCalledByAllowedUser_returnsList(
			getId("authorUser", Registration.GITHUB), Role.USER, "authorUser", Registration.GITHUB
		);
	}
	private void whenGetTestRunsOfScenario_isCalledByAllowedUser_returnsList(
			@NonNull String scenarioAuthorId, @NonNull Role role, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		String scenarioId = "scenarioId1";
		scenarioRepository.save(new Scenario(scenarioId, scenarioAuthorId, "label1", 1));
		testRunRepository.save(createTestRun("id1", scenarioId));
		testRunRepository.save(createTestRun("id2", scenarioId));
		testRunRepository.save(createTestRun("id3", scenarioId));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/%s/testrun".formatted(scenarioId))
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[ %s, %s, %s ]
				""".formatted(
						createTestRunJSON("id1", scenarioId, false),
						createTestRunJSON("id2", scenarioId, false),
						createTestRunJSON("id3", scenarioId, false)
				)));
	}

	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledWithUnknownId_returnsEmptyList() throws Exception {
		whenGetTestRunsOfScenario_returnsEmptyList("scenarioId1", "scenarioId2");
	}
	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledWithEmptyDB_returnsEmptyList() throws Exception {
		whenGetTestRunsOfScenario_returnsEmptyList(null, "scenarioId1");
	}
	private void whenGetTestRunsOfScenario_returnsEmptyList(String storedScenarioId, String requestedScenarioId) throws Exception {
		// Given
		if (storedScenarioId!=null)
			scenarioRepository.save(new Scenario(storedScenarioId, getId("author1", Registration.GITHUB), "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/%s/testrun".formatted(requestedScenarioId))
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GITHUB, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}
	
//	####################################################################################
//				addTestRun
//	####################################################################################

	@Test @DirtiesContext void whenAddTestRun_isCalledByUser_returnsStoredTestRun() throws Exception {
		whenAddTestRun_isCalledByAllowedUser_returnsStoredTestRun(
			getId("authorUser", Registration.GOOGLE), Role.USER, "authorUser", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext void whenAddTestRun_isCalledByAdmin_returnsStoredTestRun() throws Exception {
		whenAddTestRun_isCalledByAllowedUser_returnsStoredTestRun(
			getId("authorOther", Registration.GOOGLE), Role.ADMIN, "authorAdmin", Registration.GOOGLE
		);
	}
	private void whenAddTestRun_isCalledByAllowedUser_returnsStoredTestRun(
			@NonNull String storedAuthorID, @NonNull Role role, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", storedAuthorID, "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario/scenarioId1/testrun")
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTestRunJSON(null, "scenarioId1", true))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json(createTestRunJSON(null, "scenarioId1", false)))
				.andExpect(jsonPath("$.id").isString())
				.andExpect(jsonPath("$.timestamp").isString());
	}

	@Test @DirtiesContext
	void whenAddTestRun_isCalledWithUnknownScenarioId_returnsStatus404NotFound() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario/scenarioId1/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GOOGLE, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTestRunJSON(null, "scenarioId1", true))
				)

				// Then
				.andExpect(status().isNotFound());
	}

	@Test @DirtiesContext void whenAddTestRun_isCalledWithTestRunWithId_returnsStatus400BadRequest() throws Exception {
		whenAddTestRun_isCalled_returnsStatus400BadRequest(
				"scenarioId1", "notAllowedId", "scenarioId1"
		);
	}
	@Test @DirtiesContext void whenAddTestRun_isCalledWithDifferentScenarioIds_returnsStatus400BadRequest() throws Exception {
		whenAddTestRun_isCalled_returnsStatus400BadRequest(
				"scenarioId3", null, "scenarioId2"
		);
	}
	private void whenAddTestRun_isCalled_returnsStatus400BadRequest(@NonNull String scenarioIdInPath, String testRunId, @NonNull String scenarioIdInTestRun) throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario/%s/testrun".formatted(scenarioIdInPath))
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GITHUB, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTestRunJSON(testRunId, scenarioIdInTestRun, true))
				)

				// Then
				.andExpect(status().isBadRequest());
	}

	@Test @DirtiesContext
	void whenAddTestRun_isCalledUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario/%s/testrun".formatted("scenarioId1"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTestRunJSON(null, "scenarioId1", true))
				)

				// Then
				.andExpect(status().isUnauthorized());
	}

	@Test @DirtiesContext void whenAddTestRun_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenAddTestRun_returnsStatus403Forbidden(
			getId("author1", Registration.GOOGLE), Role.UNKNOWN_ACCOUNT, "author1", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext void whenAddTestRun_isCalledWithDifferentAuthorIds_returnsStatus403Forbidden() throws Exception {
		whenAddTestRun_returnsStatus403Forbidden(
			getId("authorA", Registration.GOOGLE), Role.USER, "authorB", Registration.GOOGLE
		);
	}
	private void whenAddTestRun_returnsStatus403Forbidden(
			@NonNull String storedAuthorID, @NonNull Role role, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", storedAuthorID, "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario/%s/testrun".formatted("scenarioId1"))
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTestRunJSON(null, "scenarioId1", true))
				)

				// Then
				.andExpect(status().isForbidden());
	}

//	####################################################################################
//				performTestRun
//	####################################################################################

	@Test @DirtiesContext
	void whenPerformTestRun_isCalledWithNoScenarioId_returnsStatus400BadRequest() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GITHUB, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createNewTestRunJSON(null))
				)

				// Then
				.andExpect(status().isBadRequest());
	}

	@Test @DirtiesContext
	void whenPerformTestRun_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", "author1", "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/testrun")
						.contentType(MediaType.APPLICATION_JSON)
						.content(createNewTestRunJSON("scenarioId1"))
				)

				// Then
				.andExpect(status().isUnauthorized());
	}

	@Test @DirtiesContext void whenPerformTestRun_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenPerformTestRun_isCalledByNotAllowedUser_returnsStatus403Forbidden(
			getId("author1", Registration.GOOGLE), Role.UNKNOWN_ACCOUNT, "author1", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext void whenPerformTestRun_isCalledDifferentAuthorIds_returnsStatus403Forbidden() throws Exception {
		whenPerformTestRun_isCalledByNotAllowedUser_returnsStatus403Forbidden(
			getId("authorA", Registration.GOOGLE), Role.USER, "authorB", Registration.GOOGLE
		);
	}
	private void whenPerformTestRun_isCalledByNotAllowedUser_returnsStatus403Forbidden(
			@NonNull String scenarioAuthorID, @NonNull Role role, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", scenarioAuthorID, "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/testrun")
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createNewTestRunJSON("scenarioId1"))
				)

				// Then
				.andExpect(status().isForbidden());
	}

	@Test @DirtiesContext
	void whenPerformTestRun_isCalledWithUnknownScenarioID_returnsStatus404NotFound() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", "author1", "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GITHUB, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createNewTestRunJSON("scenarioIdOther"))
				)

				// Then
				.andExpect(status().isNotFound());
	}

	@Test @DirtiesContext void whenPerformTestRun_isCalledByUser() throws Exception {
		whenPerformTestRun_isCalledByAllowedUser(
			getId("author1", Registration.GOOGLE), Role.USER, "author1", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext void whenPerformTestRun_isCalledByAdmin() throws Exception {
		whenPerformTestRun_isCalledByAllowedUser(
			getId("authorOther", Registration.GOOGLE), Role.ADMIN, "authorAdmin", Registration.GOOGLE
		);
	}
	private void whenPerformTestRun_isCalledByAllowedUser(
			@NonNull String scenarioAuthorID, @NonNull Role role, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", scenarioAuthorID, "label1", 1));
		mockWebServer.enqueue(
				ChatGptTestTools.buildApiResponse("TestAnswer", 12, 23, 35)
		);

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/testrun")
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createNewTestRunJSON("scenarioId1", "TestPrompt"))
				)

				// Then
				.andExpect(status().isOk());
	}

//	####################################################################################
//				getCurrentTestRunsOfScenario
//	####################################################################################

	@Test @DirtiesContext
	void whenGetCurrentTestRunsOfScenario_isCalledWithoutRunningTests_returnsEmptyList() throws Exception {
		whenGetCurrentTestRunsOfScenario_returnsEmptyList("scenarioId1", ()->{});
	}
	@Test @DirtiesContext
	void whenGetCurrentTestRunsOfScenario_isCalledWithoutRunningTestsOfGivenScenario_returnsEmptyList() throws Exception {
		whenGetCurrentTestRunsOfScenario_returnsEmptyList("scenarioId2", () ->
				runningTestRunsList.createNewEntry("scenarioId1")
		);
	}
	private void whenGetCurrentTestRunsOfScenario_returnsEmptyList(String scenarioId, Runnable configure) throws Exception {
		// Given
		configure.run();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/%s/testrunstate".formatted(scenarioId))
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GITHUB, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}

	@Test @DirtiesContext
	void whenGetCurrentTestRunsOfScenario_isCalled_returnsList() throws Exception {
		// Given
		RunningTestRunsList.ListEntry entry1 = runningTestRunsList.createNewEntry("scenarioId1");
		entry1.setValues(3, 4, "prompt1", "label1");

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/%s/testrunstate".formatted("scenarioId1"))
						.with(SecurityTestTools.buildUser(Role.USER, "author1", Registration.GOOGLE, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[
							{
								"promptIndex": 3,
								"totalAmountOfPrompts": 4,
								"prompt": "prompt1",
								"label": "label1"
							}
						]
				"""));
	}
}