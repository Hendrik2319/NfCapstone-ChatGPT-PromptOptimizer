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
}