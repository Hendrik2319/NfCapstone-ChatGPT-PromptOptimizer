package net.schwarzbaer.spring.promptoptimizer.backend.prompttests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService.Registration;

@SpringBootTest
@AutoConfigureMockMvc
class ScenarioIntegrationTest {

	@MockBean
	ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ScenarioRepository scenarioRepository;
	@Autowired
	private TestRunRepository testRunRepository;

	@DynamicPropertySource
	static void setUrlDynamically(DynamicPropertyRegistry reg) {
		reg.add("app.openai-api-key", ()->"dummy_api_key");
		reg.add("app.openai-api-org", ()->"dummy_api_org");
		reg.add("app.openai-api-url", ()->"dummy_url");
	}

	private void fillScenarioRepository () {
		fillScenarioRepository(null);
	}
	private void fillScenarioRepository(Registration reg2) {
		fillScenarioRepository("user1", null, "user2", reg2);
	}
	private void fillScenarioRepository(String user1, Registration reg1, String user2, Registration reg2) {
		String regId1 = reg1==null ? "registrationId1" : reg1.id;
		String regId2 = reg2==null ? "registrationId2" : reg2.id;
		scenarioRepository.save(new Scenario("id1", regId1 + user1, "label1", 1));
		scenarioRepository.save(new Scenario("id2", regId2 + user2, "label2", 1));
		scenarioRepository.save(new Scenario("id3", regId2 + user2, "label3", 1));
		scenarioRepository.save(new Scenario("id4", regId2 + user2, "label4", 1));
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

	private void performRequest_andGetStatusWithEmptyResponse(
			MockHttpServletRequestBuilder request,
			HttpStatus httpStatus
	) throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(request)

				// Then
				.andExpect(status().is(httpStatus.value()))
				.andExpect(content().string(""));
	}

	private void saveExampleScenario_performRequest_andGetStatus(
			String storedScenarioId, String storedAuthorID,
			MockHttpServletRequestBuilder request,
			HttpStatus status
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario(storedScenarioId, storedAuthorID, "label1", 1));

		// When
		mockMvc
				.perform(request)

				// Then
				.andExpect(status().is(status.value()));
	}

// ####################################################################################
//               getAllScenariosOfUser
// ####################################################################################

	@Test
	@DirtiesContext
	void whenGetAllScenariosOfUser_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		performRequest_andGetStatusWithEmptyResponse(
			MockMvcRequestBuilders
				.get("/api/scenario"),
			HttpStatus.UNAUTHORIZED
		);
	}

	@Test
	@DirtiesContext
	void whenGetAllScenariosOfUser_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		performRequest_andGetStatusWithEmptyResponse(
			MockMvcRequestBuilders
				.get("/api/scenario")
				.with(SecurityTestTools.buildUser(Role.UNKNOWN_ACCOUNT, "user2", Registration.GITHUB, "login")),
			HttpStatus.FORBIDDEN
		);
	}

	@ParameterizedTest
	@DirtiesContext
	@ArgumentsSource(SecurityTestTools.UserAndAdminRoles.class)
	void whenGetAllScenariosOfUser_isCalledByAllowedUser_returnsList(Role role) throws Exception {
		// Given
		fillScenarioRepository(Registration.GITHUB);

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario")
						.with(SecurityTestTools.buildUser(role, "user2", Registration.GITHUB, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[
							{ "id": "id2", "authorID": "%1$s", "label": "label2", "maxWantedWordCount": 1 },
							{ "id": "id3", "authorID": "%1$s", "label": "label3", "maxWantedWordCount": 1 },
							{ "id": "id4", "authorID": "%1$s", "label": "label4", "maxWantedWordCount": 1 }
						]
				""".formatted( Registration.GITHUB.id + "user2" )));
	}

// ####################################################################################
//               getAllScenarios
// ####################################################################################

	@Test
	@DirtiesContext
	void whenGetAllScenarios_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		performRequest_andGetStatusWithEmptyResponse(
			MockMvcRequestBuilders
				.get("/api/scenario/all"),
			HttpStatus.UNAUTHORIZED
		);
	}

	@ParameterizedTest
	@DirtiesContext
	@ArgumentsSource(SecurityTestTools.NotAdminRoles.class)
	void whenGetAllScenarios_isCalledByNotAllowedRole_returnsStatus403Forbidden(Role role) throws Exception {
		performRequest_andGetStatusWithEmptyResponse(
			MockMvcRequestBuilders
				.get("/api/scenario/all")
				.with(SecurityTestTools.buildUser(role, "user2", Registration.GOOGLE, "login")),
			HttpStatus.FORBIDDEN
		);
	}

	@Test
	@DirtiesContext
	void whenGetAllScenarios_isCalledByAdmin_returnsList() throws Exception {
		// Given
		Registration reg1 = Registration.GITHUB;
		Registration reg2 = Registration.GOOGLE;
		fillScenarioRepository("user1", reg1, "user2", reg2);

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/all")
						.with(SecurityTestTools.buildUser(Role.ADMIN, "user2", reg2, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[
							{ "id": "id1", "authorID": "%1$s", "label": "label1", "maxWantedWordCount":  1 },
							{ "id": "id2", "authorID": "%2$s", "label": "label2", "maxWantedWordCount":  1 },
							{ "id": "id3", "authorID": "%2$s", "label": "label3", "maxWantedWordCount":  1 },
							{ "id": "id4", "authorID": "%2$s", "label": "label4", "maxWantedWordCount":  1 }
						]
				""".formatted(reg1.id + "user1", reg2.id + "user2")));
	}

// ####################################################################################
//               addScenarios
// ####################################################################################

	@Test
	@DirtiesContext
	void whenAddScenarios_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		performRequest_andGetStatusWithEmptyResponse(
			MockMvcRequestBuilders
				.post("/api/scenario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{ "label": "labelXY" }
				"""),
			HttpStatus.UNAUTHORIZED
		);
	}

	@Test
	@DirtiesContext
	void whenAddScenarios_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		performRequest_andGetStatusWithEmptyResponse(
			MockMvcRequestBuilders
				.post("/api/scenario")
				.with(SecurityTestTools.buildUser(Role.UNKNOWN_ACCOUNT, "userId", Registration.GOOGLE, "login"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{ "label": "labelXY" }
				"""),
			HttpStatus.FORBIDDEN
		);
	}

	@ParameterizedTest
	@DirtiesContext
	@ArgumentsSource(SecurityTestTools.UserAndAdminRoles.class)
	void whenAddScenarios_isCalledByAllowedUser_returnsList(Role role) throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario")
						.with(SecurityTestTools.buildUser(role, "userId", Registration.GITHUB, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "label": "labelXY" }
						""")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "authorID": "%s", "label": "labelXY" }
				""".formatted(Registration.GITHUB.id + "userId")));
	}

// ####################################################################################
//               updateScenario
// ####################################################################################

	@Test @DirtiesContext
	void whenUpdateScenario_getsPathIdDifferentToScenarioID_returnsStatus400BadRequest() throws Exception {
		whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
			"id2",
			"{ \"id\": \"id1\", \"authorID\": \"%s\", \"label\": \"labelNew\" }".formatted(Registration.GITHUB.id + "userId1"),
			"userId1", Registration.GITHUB
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_getsScenarioWithNoId_returnsStatus400BadRequest() throws Exception {
		whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
			"id1",
			"{ \"authorID\": \"%s\", \"label\": \"labelNew\" }".formatted(Registration.GITHUB.id + "userId1"),
			"userId1", Registration.GITHUB
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_getsScenarioWithNoAuthorId_returnsStatus400BadRequest() throws Exception {
		whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
			"id1",
			"{ \"id\": \"id1\", \"label\": \"labelNew\" }",
			"userId1", Registration.GITHUB
		);
	}
	private void whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
			String pathId, String requestBody, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted(pathId))
						.with(SecurityTestTools.buildUser(Role.USER, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)

				// Then
				.andExpect(status().isBadRequest());
	}

	@Test
	@DirtiesContext
	void whenUpdateScenario_getsScenarioWithUnknownId_returnsStatus404NotFound() throws Exception {
		// Given
		String userId = "userId1";
		Registration reg = Registration.GOOGLE;
		String authorID = reg.id + userId;

		scenarioRepository.save(new Scenario("id2", authorID, "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(Role.USER, userId, reg, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"id\": \"id1\", \"authorID\": \"%s\", \"label\": \"labelNew\" }".formatted(authorID))
				)

				// Then
				.andExpect(status().isNotFound());
	}

	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id1", "author1",
			MockMvcRequestBuilders
				.put("/api/scenario/%s".formatted("id1"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{ "id": "id1", "authorID": "%s", "label": "labelNew" }
				""".formatted("author1")),
			HttpStatus.UNAUTHORIZED
		);
	}

	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.UNKNOWN_ACCOUNT, "userId1", "userId1", "userId1", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUser_withDbIdDifferentToGivenScenario_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.USER, "userId1", "userId2", "userId1", Registration.GOOGLE
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUser_withDbIdDifferentToStoredScenario_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.USER, "userId2", "userId1", "userId1", Registration.GOOGLE
		);
	}
	private void whenUpdateScenario_isCalled_returnsStatus403Forbidden(
			@NonNull Role role, @NonNull String userIdOfStored, @NonNull String userIdOfGiven, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		String authorOfStored = registration.id + userIdOfStored;
		String authorOfGiven  = registration.id + userIdOfGiven;
		scenarioRepository.save(new Scenario("id1", authorOfStored, "labelOld", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "id": "id1", "authorID": "%s", "label": "labelNew", "maxWantedWordCount": 1 }
						""".formatted(authorOfGiven))
				)

				// Then
				.andExpect(status().isForbidden());
	}

	@Test @DirtiesContext void whenUpdateScenario_isCalledByAdmin_returnsUpdatedValue() throws Exception {
		whenUpdateScenario_isCalledByAllowedUser_returnsUpdatedValue(Role.ADMIN, Registration.GITHUB.id + "userId2", "userId1", Registration.GITHUB);
	}
	@Test @DirtiesContext void whenUpdateScenario_isCalledByUser_returnsUpdatedValue() throws Exception {
		whenUpdateScenario_isCalledByAllowedUser_returnsUpdatedValue(Role.USER, Registration.GITHUB.id + "userId1", "userId1", Registration.GITHUB);
	}
	private void whenUpdateScenario_isCalledByAllowedUser_returnsUpdatedValue(
			@NonNull Role role, @NonNull String storedAuthorID, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "labelOld", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "id": "id1", "authorID": "%s", "label": "labelNew", "maxWantedWordCount": 1 }
						""".formatted(storedAuthorID))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "id": "id1", "authorID": "%s", "label": "labelNew", "maxWantedWordCount": 1 }
				""".formatted(storedAuthorID)));

		Optional<Scenario> actual = scenarioRepository.findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id1", storedAuthorID, "labelNew", 1);
		assertEquals(expected, actual.get());
	}

// ####################################################################################
//               deleteScenario
// ####################################################################################

	@Test @DirtiesContext void whenDeleteScenario_isCalledByAdmin() throws Exception {
		whenDeleteScenario_isCalledByAllowedUser(Role.ADMIN, Registration.GITHUB.id + "userId2", "userId1", Registration.GITHUB);
	}
	@Test @DirtiesContext void whenDeleteScenario_isCalledByUser() throws Exception {
		whenDeleteScenario_isCalledByAllowedUser(Role.USER, Registration.GITHUB.id + "userId1", "userId1", Registration.GITHUB);
	}
	private void whenDeleteScenario_isCalledByAllowedUser(
			@NonNull Role role, @NonNull String storedAuthorID, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "label1", 1));
		testRunRepository.save(createTestRun("testRun1", "id1"));
		testRunRepository.save(createTestRun("testRun2", "id1"));
		testRunRepository.save(createTestRun("testRun3", "id1"));
		testRunRepository.save(createTestRun("testRun4", "id1"));
		testRunRepository.save(createTestRun("testRun5", "id2"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.delete("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
				)

				// Then
				.andExpect(status().isOk());

		Optional<Scenario> actual = scenarioRepository.findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isEmpty());

		List<TestRun> actualRemainingTestRuns = testRunRepository.findAll();
		List<TestRun> expectedRemainingTestRuns = List.of( createTestRun("testRun5", "id2") );
		assertEquals(expectedRemainingTestRuns, actualRemainingTestRuns);
	}

	@Test @DirtiesContext
	void whenDeleteScenario_isCalledWithUnknownId_returnsStatus404Notfound() throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id2", "author1",
			MockMvcRequestBuilders
				.delete("/api/scenario/id1")
				.with(SecurityTestTools
					.buildUser(Role.USER, "userId1", Registration.GOOGLE, "login")
				),
			HttpStatus.NOT_FOUND
		);
	}

	@Test @DirtiesContext
	void whenDeleteScenario_isCalledUnauthorized_returnStatus401Unauthorized() throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id1", "author1",
			MockMvcRequestBuilders
				.delete("/api/scenario/id1"),
			HttpStatus.UNAUTHORIZED
		);
	}

	@Test @DirtiesContext void whenDeleteScenario_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenDeleteScenario_isCalled_returnsStatus403Forbidden(Role.UNKNOWN_ACCOUNT, Registration.GITHUB.id + "userId1", "userId1", Registration.GITHUB);
	}
	@Test @DirtiesContext void whenDeleteScenario_isCalledWithDifferentAuthorIDs_returnsStatus403Forbidden() throws Exception {
		whenDeleteScenario_isCalled_returnsStatus403Forbidden(Role.USER, Registration.GITHUB.id + "userId2", "userId1", Registration.GITHUB);
	}
	private void whenDeleteScenario_isCalled_returnsStatus403Forbidden(
			Role role, @NonNull String storedAuthorID, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id1", storedAuthorID,
			MockMvcRequestBuilders
				.get("/api/scenario/id1")
				.with(SecurityTestTools
					.buildUser(role, userId, registration, "login")
				),
			HttpStatus.FORBIDDEN
		);
	}

// ####################################################################################
//               getScenarioById
// ####################################################################################

	@Test @DirtiesContext void whenGetScenarioById_isCalledByAdmin() throws Exception {
		whenGetScenarioById_isCalledByAllowedUser(Role.ADMIN, Registration.GITHUB.id + "userId2", "userId1", Registration.GITHUB);
	}
	@Test @DirtiesContext void whenGetScenarioById_isCalledByUser() throws Exception {
		whenGetScenarioById_isCalledByAllowedUser(Role.USER, Registration.GITHUB.id + "userId1", "userId1", Registration.GITHUB);
	}

	private void whenGetScenarioById_isCalledByAllowedUser(
			@NonNull Role role, @NonNull String storedAuthorID, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "label1", 1));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(role, userId, registration, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "id": "id1", "authorID": "%s", "label": "label1", "maxWantedWordCount": 1 }
				""".formatted(storedAuthorID)));
	}

	@Test @DirtiesContext
	void whenGetScenarioById_isCalledWithUnknownId_returnsStatus404NotFound() throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id2", "author1",
			MockMvcRequestBuilders
				.get("/api/scenario/id1")
				.with(SecurityTestTools
					.buildUser(Role.USER, "userId1", Registration.GOOGLE, "login")
				),
			HttpStatus.NOT_FOUND
		);
	}

	@Test @DirtiesContext
	void whenGetScenarioById_isCalledUnauthorized_returnsStatus401Unauthorized() throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id1", "author1",
			MockMvcRequestBuilders
				.get("/api/scenario/id1"),
			HttpStatus.UNAUTHORIZED
		);
	}

	@Test @DirtiesContext void whenGetScenarioById_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenGetScenarioById_isCalled_returnsStatus403Forbidden(Role.UNKNOWN_ACCOUNT, Registration.GITHUB.id + "userId1", "userId1", Registration.GITHUB);
	}
	@Test @DirtiesContext void whenGetScenarioById_isCalledWithDifferentAuthorIDs_returnsStatus403Forbidden() throws Exception {
		whenGetScenarioById_isCalled_returnsStatus403Forbidden(Role.USER, Registration.GITHUB.id + "userId2", "userId1", Registration.GITHUB);
	}
	private void whenGetScenarioById_isCalled_returnsStatus403Forbidden(
			Role role, @NonNull String storedAuthorID, @NonNull String userId, @NonNull Registration registration
	) throws Exception {
		saveExampleScenario_performRequest_andGetStatus(
			"id1", storedAuthorID,
			MockMvcRequestBuilders
				.get("/api/scenario/id1")
				.with(SecurityTestTools
					.buildUser(role, userId, registration, "login")
				),
			HttpStatus.FORBIDDEN
		);
	}
}
