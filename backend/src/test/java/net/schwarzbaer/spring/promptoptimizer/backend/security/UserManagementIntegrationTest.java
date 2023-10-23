package net.schwarzbaer.spring.promptoptimizer.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserManagementIntegrationTest {

	@MockBean
	ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private MockMvc mockMvc;

	@DynamicPropertySource
	static void setUrlDynamically(DynamicPropertyRegistry reg) {
		reg.add("app.openai-api-key", ()->"dummy_api_key");
		reg.add("app.openai-api-org", ()->"dummy_api_org");
		reg.add("app.openai-api-url", ()->"dummy_url");
	}

	@Test
	@WithMockUser(username="TestUser")
	void whenGetMe_isCalledWithAuthenticatedUser() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users/me")
						.with(oidcLogin()
								.authorities(new SimpleGrantedAuthority("TestRole"))
								.userInfoToken( token -> token
										.claim("id", "TestID")
										.claim("login", "TestLogin")
								)
						)
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
					{
						"isAuthenticated":true,
						"id":"TestID",
						"login":"TestLogin",
						"name":null,
						"location":null,
						"url":null,
						"avatar_url":null
					}
				"""))
		;
	}

	@Test
	@WithMockUser(username="TestUser")
	void whenGetMe_isCalledWithMockUser() throws Exception { // to test the test
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users/me")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
					{
						"isAuthenticated":false,
						"id":"TestUser",
						"login":null,
						"name":null,
						"location":null,
						"url":null,
						"avatar_url":null
					}
				"""))
		;
	}

	@Test
	void whenGetMe_isCalledWithNoUser() throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users/me")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("""
					{
						"isAuthenticated":false,
						"id":"anonymousUser",
						"login":null,
						"name":null,
						"location":null,
						"url":null,
						"avatar_url":null
					}
				"""))
		;
	}
}