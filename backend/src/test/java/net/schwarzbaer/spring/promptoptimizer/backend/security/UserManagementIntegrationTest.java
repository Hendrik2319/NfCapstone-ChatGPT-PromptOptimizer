package net.schwarzbaer.spring.promptoptimizer.backend.security;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

	@NonNull
	private static String buildCurrentUserResponse(
			boolean isAuthenticated, boolean isUser, boolean isAdmin,
			@NonNull String id, @Nullable String login
	) {
		return """
					{
						"isAuthenticated": %s,
						"isUser"         : %s,
						"isAdmin"        : %s,
						"id"        : "%s",
						"userDbId"  : null,
						"login"     : %s,
						"name"      : null,
						"location"  : null,
						"url"       : null,
						"avatar_url": null
					}
				"""
				.formatted(
						isAuthenticated,
						isUser,
						isAdmin,
						id,
						login==null ? null : "\"%s\"".formatted( login )
				);
	}

// ####################################################################################
//               UserController.getCurrentUser
// ####################################################################################

	@Test
	void whenGetCurrentUser_isCalledWithNoUser() throws Exception {
		whenGetCurrentUser_isCalledWithUnauthenticatedUser("anonymousUser");
	}
	@Test
	@WithMockUser(username="TestUser")
	void whenGetCurrentUser_isCalledWithMockUser() throws Exception { // to test the test
		whenGetCurrentUser_isCalledWithUnauthenticatedUser("TestUser");
	}
	private void whenGetCurrentUser_isCalledWithUnauthenticatedUser(String id) throws Exception {
		// Given

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users/me")
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json(buildCurrentUserResponse(
						false, false, false,
						id, null)
				))
		;
	}

	@Test void whenGetCurrentUser_isCalledWithAuthenticatedUser() throws Exception {
		whenGetCurrentUser_isCalledWithAuthenticatedUser(null, false, false);
	}
	@Test void whenGetCurrentUser_isCalledWithUnknownAccount() throws Exception {
		whenGetCurrentUser_isCalledWithAuthenticatedUser(Role.UNKNOWN_ACCOUNT, false, false);
	}
	@Test void whenGetCurrentUser_isCalledWithUSER() throws Exception {
		whenGetCurrentUser_isCalledWithAuthenticatedUser(Role.USER, true, false);
	}
	@Test void whenGetCurrentUser_isCalledWithADMIN() throws Exception {
		whenGetCurrentUser_isCalledWithAuthenticatedUser(Role.ADMIN, false, true);
	}
	private void whenGetCurrentUser_isCalledWithAuthenticatedUser(Role role, boolean isUser, boolean isAdmin) throws Exception {
		// Given

		// When
		mockMvc
				.perform( MockMvcRequestBuilders
						.get("/api/users/me")
						.with(SecurityTestTools.buildUser(role, "TestID", "TestLogin"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json(buildCurrentUserResponse(
						true, isUser, isAdmin,
						"TestID", "TestLogin")
				))
		;
	}

// ####################################################################################
//               StoredUserInfoController.getAllStoredUsers
// ####################################################################################

// ####################################################################################
//               StoredUserInfoController.updateStoredUser
// ####################################################################################

// ####################################################################################
//               StoredUserInfoController.deleteStoredUser
// ####################################################################################

// ####################################################################################
//               StoredUserInfoController.getDenialReasonForCurrentUser
// ####################################################################################

}