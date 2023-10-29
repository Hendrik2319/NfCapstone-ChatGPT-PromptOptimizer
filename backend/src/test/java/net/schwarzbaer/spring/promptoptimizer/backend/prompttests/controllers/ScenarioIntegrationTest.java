package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScenarioIntegrationTest {

	@MockBean
	ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ScenarioRepository scenarioRepository;

	@DynamicPropertySource
	static void setUrlDynamically(DynamicPropertyRegistry reg) {
		reg.add("app.openai-api-key", ()->"dummy_api_key");
		reg.add("app.openai-api-org", ()->"dummy_api_org");
		reg.add("app.openai-api-url", ()->"dummy_url");
	}

// ####################################################################################
//               getAllScenariosOfUser
// ####################################################################################

	@Test
	@DirtiesContext
	void whenGetAllScenariosOfUser_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario")
				)

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
				.andExpect(content().string(""));
	}

	@Test
	@DirtiesContext
	void whenGetAllScenariosOfUser_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario")
						.with(SecurityTestTools.buildUser(Role.UNKNOWN_ACCOUNT, "id", "author2", "login"))
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
				.andExpect(content().string(""));
	}

	@ParameterizedTest
	@DirtiesContext
	@ArgumentsSource(SecurityTestTools.UserAndAdminRoles.class)
	void whenGetAllScenariosOfUser_isCalledByAllowedUser_returnsList(Role role) throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario")
						.with(SecurityTestTools.buildUser(role, "id", "author2", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[
							{ "id": "id2", "authorID": "author2", "label": "label2" },
							{ "id": "id3", "authorID": "author2", "label": "label3" },
							{ "id": "id4", "authorID": "author2", "label": "label4" }
						]
				"""));
	}

// ####################################################################################
//               getAllScenarios
// ####################################################################################

	@Test
	@DirtiesContext
	void whenGetAllScenarios_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders.get("/api/scenario/all"))

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
				.andExpect(content().string(""));
	}

	@ParameterizedTest
	@DirtiesContext
	@ArgumentsSource(SecurityTestTools.NotAdminRoles.class)
	void whenGetAllScenarios_isCalledByNotAllowedRole_returnsStatus403Forbidden(Role role) throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/all")
						.with(SecurityTestTools.buildUser(role, "id", "dbId", "login"))
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
				.andExpect(content().string(""));
	}

	@Test
	@DirtiesContext
	void whenGetAllScenarios_isCalledByAdmin_returnsList() throws Exception {
		// Given
		fillScenarioRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/all")
						.with(SecurityTestTools.buildUser(Role.ADMIN, "id", "dbId", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[
							{ "id": "id1", "authorID": "author1", "label": "label1" },
							{ "id": "id2", "authorID": "author2", "label": "label2" },
							{ "id": "id3", "authorID": "author2", "label": "label3" },
							{ "id": "id4", "authorID": "author2", "label": "label4" }
						]
				"""));
	}

	private void fillScenarioRepository() {
		scenarioRepository.save(new Scenario("id1", "author1", "label1"));
		scenarioRepository.save(new Scenario("id2", "author2", "label2"));
		scenarioRepository.save(new Scenario("id3", "author2", "label3"));
		scenarioRepository.save(new Scenario("id4", "author2", "label4"));
	}

// ####################################################################################
//               addScenarios
// ####################################################################################

	@Test
	@DirtiesContext
	void whenAddScenarios_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "label": "labelXY" }
						""")
				)

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
				.andExpect(content().string(""));
	}

	@Test
	@DirtiesContext
	void whenAddScenarios_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.post("/api/scenario")
						.with(SecurityTestTools.buildUser(Role.UNKNOWN_ACCOUNT, "id", "userXY", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "label": "labelXY" }
						""")
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
				.andExpect(content().string(""));
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
						.with(SecurityTestTools.buildUser(role, "id", "userXY", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "label": "labelXY" }
						""")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "authorID": "userXY", "label": "labelXY" }
				"""));
	}

// ####################################################################################
//               updateScenario
// ####################################################################################

	@Test @DirtiesContext
	void whenUpdateScenario_getsPathIdDifferentToScenarioID_returnsStatus400BadRequest() throws Exception {
		whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
				"id2","{ \"id\": \"id1\", \"authorID\": \"author1\", \"label\": \"labelNew\" }"
		);
	}

	@Test @DirtiesContext
	void whenUpdateScenario_getsScenarioWithNoId_returnsStatus400BadRequest() throws Exception {
		whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
				"id1","{ \"authorID\": \"author1\", \"label\": \"labelNew\" }"
		);
	}

	@Test @DirtiesContext
	void whenUpdateScenario_getsScenarioWithNoAuthorId_returnsStatus400BadRequest() throws Exception {
		whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
				"id1","{ \"id\": \"id1\", \"label\": \"labelNew\" }"
		);
	}

	private void whenUpdateScenario_getsWrongArguments_returnsStatus400BadRequest(
			String pathId, String requestBody
	) throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted(pathId))
						.with(SecurityTestTools.buildUser(Role.USER, "userId1", "author1", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)

				// Then
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	@DirtiesContext
	void whenUpdateScenario_getsScenarioWithUnknownId_returnsStatus404NotFound() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id2", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(Role.USER, "userId1", "author1", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"id\": \"id1\", \"authorID\": \"author1\", \"label\": \"labelNew\" }")
				)

				// Then
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", "author1", "labelOld"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "id": "id1", "authorID": "%s", "label": "labelNew" }
						""".formatted("author1"))
				)

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
	}

	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.UNKNOWN_ACCOUNT, "author1", "author1", "author1"
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByNonAdmin_withNoDbId_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.USER, null, "author1", "author1"
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByNonAdmin_withDbIdDifferentToGivenScenario_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.USER, "author1", "author1", "author2"
		);
	}
	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByNonAdmin_withDbIdDifferentToStoredScenario_returnsStatus403Forbidden() throws Exception {
		whenUpdateScenario_isCalled_returnsStatus403Forbidden(
				Role.USER,"author1", "author2", "author1"
		);
	}

	private void whenUpdateScenario_isCalled_returnsStatus403Forbidden(
			Role role, String userDbId, String authorOfStored, String authorOfGiven
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", authorOfStored, "labelOld"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(role, "userId1", userDbId, "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "id": "id1", "authorID": "%s", "label": "labelNew" }
						""".formatted(authorOfGiven))
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}

	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByAdmin_returnsUpdatedValue() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", "author2", "labelOld"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "authorAdmin", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "id": "id1", "authorID": "author1", "label": "labelNew" }
						""")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "id": "id1", "authorID": "author1", "label": "labelNew" }
				"""));

		Optional<Scenario> actual = scenarioRepository.findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id1", "author1", "labelNew");
		assertEquals(expected, actual.get());
	}

	@Test @DirtiesContext
	void whenUpdateScenario_isCalledByUser_returnsUpdatedValue() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", "author1", "labelOld"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/scenario/%s".formatted("id1"))
						.with(SecurityTestTools.buildUser(Role.USER, "userId1", "author1", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "id": "id1", "authorID": "author1", "label": "labelNew" }
						""")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "id": "id1", "authorID": "author1", "label": "labelNew" }
				"""));

		Optional<Scenario> actual = scenarioRepository.findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id1", "author1", "labelNew");
		assertEquals(expected, actual.get());
	}

// ####################################################################################
//               deleteScenario
// ####################################################################################

	@Test @DirtiesContext void whenDeleteScenario_isCalledByAdmin() throws Exception {
		whenDeleteScenario_isCalledByAllowedUser(Role.ADMIN, "authorAdmin", "author2");
	}
	@Test @DirtiesContext void whenDeleteScenario_isCalledByUser() throws Exception {
		whenDeleteScenario_isCalledByAllowedUser(Role.USER, "author1", "author1");
	}

	private void whenDeleteScenario_isCalledByAllowedUser(
			Role role, String userDbId, String storedAuthorID
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.delete("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(role, "userId1", userDbId, "login"))
				)

				// Then
				.andExpect(status().isOk());

		Optional<Scenario> actual = scenarioRepository.findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test @DirtiesContext
	void whenDeleteScenario_isCalledWithUnknownId_returnsStatus404Notfound() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id2", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.delete("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(Role.USER, "userId1", "author1", "login"))
				)

				// Then
				.andExpect(status().isNotFound());
	}

	@Test @DirtiesContext
	void whenDeleteScenario_isCalledUnauthorized_returnStatus401Unauthorized() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.delete("/api/scenario/id1")
				)

				// Then
				.andExpect(status().isUnauthorized());
	}

	@Test @DirtiesContext void whenDeleteScenario_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenDeleteScenario_isCalled_returnsStatus403Forbidden(Role.UNKNOWN_ACCOUNT, "author1", "author1");
	}
	@Test @DirtiesContext void whenDeleteScenario_isCalledByUserWithNoDbIDs_returnsStatus403Forbidden() throws Exception {
		whenDeleteScenario_isCalled_returnsStatus403Forbidden(Role.USER, null, "author2");
	}
	@Test @DirtiesContext void whenDeleteScenario_isCalledWithDifferentAuthorIDs_returnsStatus403Forbidden() throws Exception {
		whenDeleteScenario_isCalled_returnsStatus403Forbidden(Role.USER, "author2", "author1");
	}

	private void whenDeleteScenario_isCalled_returnsStatus403Forbidden(
			Role role, String userDbId, String storedAuthorID
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.delete("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(role, "userId1", userDbId, "login"))
				)

				// Then
				.andExpect(status().isForbidden());
	}

// ####################################################################################
//               getScenarioById
// ####################################################################################

	@Test @DirtiesContext void whenGetScenarioById_isCalledByAdmin() throws Exception {
		whenGetScenarioById_isCalledByAllowedUser(Role.ADMIN, "authorAdmin", "author2");
	}
	@Test @DirtiesContext void whenGetScenarioById_isCalledByUser() throws Exception {
		whenGetScenarioById_isCalledByAllowedUser(Role.USER, "author1", "author1");
	}

	private void whenGetScenarioById_isCalledByAllowedUser(
			Role role, String userDbId, String storedAuthorID
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(role, "userId1", userDbId, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{ "id": "id1", "authorID": "%s", "label": "label1" }
				""".formatted(storedAuthorID)));
	}

	@Test @DirtiesContext
	void whenGetScenarioById_isCalledWithUnknownId_returnsStatus404NotFound() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id2", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(Role.USER, "userId1", "author1", "login"))
				)

				// Then
				.andExpect(status().isNotFound());
	}

	@Test @DirtiesContext
	void whenGetScenarioById_isCalledUnauthorized_returnsStatus401Unauthorized() throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", "author1", "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id1")
				)

				// Then
				.andExpect(status().isUnauthorized());
	}

	@Test @DirtiesContext void whenGetScenarioById_isCalledByUnknownAccount_returnsStatus403Forbidden() throws Exception {
		whenGetScenarioById_isCalled_returnsStatus403Forbidden(Role.UNKNOWN_ACCOUNT, "author1", "author1");
	}
	@Test @DirtiesContext void whenGetScenarioById_isCalledByUserWithNoDbIDs_returnsStatus403Forbidden() throws Exception {
		whenGetScenarioById_isCalled_returnsStatus403Forbidden(Role.USER, null, "author2");
	}
	@Test @DirtiesContext void whenGetScenarioById_isCalledWithDifferentAuthorIDs_returnsStatus403Forbidden() throws Exception {
		whenGetScenarioById_isCalled_returnsStatus403Forbidden(Role.USER, "author2", "author1");
	}

	private void whenGetScenarioById_isCalled_returnsStatus403Forbidden(
			Role role, String userDbId, String storedAuthorID
	) throws Exception {
		// Given
		scenarioRepository.save(new Scenario("id1", storedAuthorID, "label1"));

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/scenario/id1")
						.with(SecurityTestTools.buildUser(role, "userId1", userDbId, "login"))
				)

				// Then
				.andExpect(status().isForbidden());
	}
}
