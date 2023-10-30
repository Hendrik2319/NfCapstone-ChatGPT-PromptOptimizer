package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestRunIntegrationTest {

	@MockBean ClientRegistrationRepository clientRegistrationRepository;

	@Autowired private MockMvc mockMvc;
	@Autowired private ScenarioRepository scenarioRepository;
	@Autowired private TestRunRepository testRunRepository;


	@DynamicPropertySource
	static void setUrlDynamically(DynamicPropertyRegistry reg) {
		reg.add("app.openai-api-key", ()->"dummy_api_key");
		reg.add("app.openai-api-org", ()->"dummy_api_org");
		reg.add("app.openai-api-url", ()->"dummy_url");
	}

	@NonNull
	private static TestRun createTestRun(String testRunId, String scenarioId) {
		return new TestRun(
				testRunId, scenarioId,
				ZonedDateTime.of(2023, 10, 29, 14, 30, 0, 0, ZoneId.systemDefault()),
				"prompt", List.of("var1", "var2"),
				List.of(Map.of("var1", List.of("value1"), "var2", List.of("value2"))),
				List.of(new TestRun.TestAnswer(1, "label", "answer"))
		);
	}

	private String createTestRunResponse(String testRunId, String scenarioId) {
		return """
			{
				"id"        : "%s",
			    "scenarioId": "%s",
			    "prompt"    : "prompt",
			    "variables" : ["var1","var2"],
			    "testcases" : [{"var1":["value1"],"var2":["value2"]}],
			    "answers"   : [{"indexOfTestCase":1,"label":"label","answer":"answer"}]
			}
		""".formatted(
				testRunId,
				scenarioId
		);
	}

	/* ####################################################################################
				getTestRunsOfScenario
	#################################################################################### */

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
				.andExpect(status().is(401))
				.andExpect(content().string(""));
	}

	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenGetTestRunsOfScenario_isCalled_returnsStatus403Forbidden(
				"author1", "author1", Role.UNKNOWN_ACCOUNT
		);
	}

	@Test @DirtiesContext void whenGetTestRunsOfScenario_isCalledDifferentAuthorIds_returnsStatus403Forbidden() throws Exception {
		whenGetTestRunsOfScenario_isCalled_returnsStatus403Forbidden(
				"author3", "author2", Role.USER
		);
	}

	private void whenGetTestRunsOfScenario_isCalled_returnsStatus403Forbidden(String storedAuthorID, String currentUserDbId, Role role) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", storedAuthorID, "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/%s/testrun".formatted("scenarioId1"))
						.with(SecurityTestTools.buildUser(role, "id", currentUserDbId, "login"))
				)

				// Then
				.andExpect(status().is(403));
	}

	@Test
	@DirtiesContext
	void whenGetTestRunsOfScenario_isCalledByAdmin_returnsList() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", "author1", "label1"));
		testRunRepository.save(createTestRun("id1", "scenarioId1"));
		testRunRepository.save(createTestRun("id2", "scenarioId1"));
		testRunRepository.save(createTestRun("id3", "scenarioId1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/scenarioId1/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "id", "authorAdmin", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[ %s, %s, %s ]
				""".formatted(
						createTestRunResponse("id1", "scenarioId1"),
						createTestRunResponse("id2", "scenarioId1"),
						createTestRunResponse("id3", "scenarioId1")
				)));
	}

	@Test
	@DirtiesContext
	void whenGetTestRunsOfScenario_isCalledWithUnknownId_returnsEmptyList() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id2/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "id", "author1", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[]
				"""));
	}

	@Test
	@DirtiesContext
	void whenGetTestRunsOfScenario_isCalledFilledDB_returnsList() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", "author1", "label1"));
		testRunRepository.save(createTestRun("id1", "scenarioId1"));
		testRunRepository.save(createTestRun("id2", "scenarioId1"));
		testRunRepository.save(createTestRun("id3", "scenarioId1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/scenarioId1/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "id", "author1", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[ %s, %s, %s ]
				""".formatted(
						createTestRunResponse("id1", "scenarioId1"),
						createTestRunResponse("id2", "scenarioId1"),
						createTestRunResponse("id3", "scenarioId1")
				)));
	}

	@Test
	@DirtiesContext
	void whenGetTestRunsOfScenario_isCalledWithEmptyDB_returnsEmptyList() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("scenarioId1", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/scenarioId1/testrun")
						.with(SecurityTestTools.buildUser(Role.USER, "id", "author1", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[]
				"""));
	}

	/* ####################################################################################
				addTestRun
	#################################################################################### */

	@Test
	@DirtiesContext
	void addTestRun() {
	}

}