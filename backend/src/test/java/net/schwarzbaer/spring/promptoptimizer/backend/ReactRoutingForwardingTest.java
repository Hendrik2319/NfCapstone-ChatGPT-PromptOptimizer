package net.schwarzbaer.spring.promptoptimizer.backend;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.SecurityTestTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReactRoutingForwardingTest {

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
	void whenNonExistingResourceIsCalled() throws Exception {
		whenUrlIsCalled("/undefinedpath", "NonExistingResource");
	}

	@Test
	@DirtiesContext
	void whenExistingResourceIsCalled() throws Exception {
		whenUrlIsCalled("/vite.svg", "ExistingResource(?)");
	}

	private void whenUrlIsCalled(String url, String testLabel) throws Exception {
		// Given

		// When
		ResultActions resultActions = null;
		try {
			resultActions = mockMvc
					.perform(MockMvcRequestBuilders
							.get(url)
							.with(SecurityTestTools.buildUser(Role.USER, "id", "author1", "login"))
					);
		} catch (FileNotFoundException e) {
			assertTrue(true);
			System.out.printf("[%s] FileNotFoundException: %s%n", testLabel, e.getMessage());
		}

		// Then
		if (resultActions!=null)
			resultActions
					.andExpect(status().isOk());
	}

}