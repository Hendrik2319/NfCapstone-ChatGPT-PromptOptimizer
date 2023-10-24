package net.schwarzbaer.spring.promptoptimizer.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TemporaryRestrictionTestTest {
	// Diese Tests fliegen raus, sobald der Endpunkt "/api/users/restricted" gelöscht ist.

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

	@NonNull
	public static MockHttpServletRequestBuilder buildGetRestrictedRequest(@Nullable Role role, @NonNull String id, @NonNull String login) {
		return MockMvcRequestBuilders
				.get("/api/users/restricted")
				.with(SecurityTestTools.buildUser(role, id, login));
	}

	@NonNull
	public static MockHttpServletRequestBuilder buildGetRestrictedRequest() {
		return MockMvcRequestBuilders
				.get("/api/users/restricted");
	}

	@Test
	void whenGetRestricted_isCalledWithNoUser() throws Exception {
		// Given

		// When
		mockMvc
				.perform(
						buildGetRestrictedRequest()
				)

				// Then
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
		;
	}

	@Test
	@WithMockUser(username="TestUser")
	void whenGetRestricted_isCalledWithMockUser() throws Exception { // to test the test
		// Given

		// When
		mockMvc
				.perform(
						buildGetRestrictedRequest()
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
		;
	}

	@Test
	void whenGetRestricted_isCalledWithAuthenticatedUser() throws Exception {
		// Given

		// When
		mockMvc
				.perform(
						buildGetRestrictedRequest(null, "TestID", "TestLogin")
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
		;
	}

	@Test
	void whenGetRestricted_isCalledWithUnknownAccount() throws Exception {
		// Given

		// When
		mockMvc
				.perform(
						buildGetRestrictedRequest(Role.UNKNOWN_ACCOUNT, "TestID", "TestLogin")
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
		;
	}

	@Test
	void whenGetRestricted_isCalledWithUser() throws Exception {
		// Given

		// When
		mockMvc
				.perform(
						buildGetRestrictedRequest(Role.USER, "TestID", "TestLogin")
				)

				// Then
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
		;
	}

	@Test
	void whenGetRestricted_isCalledWithAdmin() throws Exception {
		// Given

		// When
		mockMvc
				.perform(
						buildGetRestrictedRequest(Role.ADMIN, "TestID", "TestLogin")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().string("You got access to a restricted endpoint"))
		;
	}
}