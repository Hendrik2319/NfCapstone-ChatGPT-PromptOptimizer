package net.schwarzbaer.spring.promptoptimizer.backend.security;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.StoredUserInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserManagementIntegrationTest {

	@MockBean
	ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private StoredUserInfoRepository storedUserInfoRepository;

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

	@NonNull
	private static StoredUserInfo createStoredUserInfo(Role role, String registrationId, String originalId, int index) {
		return new StoredUserInfo(
				registrationId + originalId, role, registrationId, originalId,
				"login" + index, "name" + index, "location" + index, "url" + index,
				"avatarUrl" + index, "reason" + index
		);
	}

	private String buildResponse(StoredUserInfo storedUserInfo) {
		List<String> valueStrs = new ArrayList<>();
		addValue(valueStrs, storedUserInfo.id            (), "id"            , "\"%s\"");
		addValue(valueStrs, storedUserInfo.role          (), "role"          , "\"%s\"");
		addValue(valueStrs, storedUserInfo.registrationId(), "registrationId", "\"%s\"");
		addValue(valueStrs, storedUserInfo.originalId    (), "originalId"    , "\"%s\"");
		addValue(valueStrs, storedUserInfo.login         (), "login"         , "\"%s\"");
		addValue(valueStrs, storedUserInfo.name          (), "name"          , "\"%s\"");
		addValue(valueStrs, storedUserInfo.location      (), "location"      , "\"%s\"");
		addValue(valueStrs, storedUserInfo.avatar_url    (), "avatar_url"    , "\"%s\"");
		addValue(valueStrs, storedUserInfo.denialReason  (), "denialReason"  , "\"%s\"");
		return "{ %s }".formatted( String.join(", ", valueStrs) );
	}

	private void addValue(List<String> valueStrs, Object value, String valueName, String format) {
		if (value!=null)
			valueStrs.add( ("\"%s\": "+format).formatted(valueName, value) );
	}

	private void fillStoredUserInfoRepository() {
		storedUserInfoRepository.save(createStoredUserInfo(Role.ADMIN          , "registrationId", "userId1", 1));
		storedUserInfoRepository.save(createStoredUserInfo(Role.USER           , "registrationId", "userId2", 2));
		storedUserInfoRepository.save(createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "registrationId", "userId3", 3));
	}

	@Test
	@DirtiesContext
	void whenGetAllStoredUsers_isCalledByAdmin_returnsList() throws Exception {
		// Given
		fillStoredUserInfoRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users")
						.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "registrationIduserId1", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json("[ %s, %s, %s ]".formatted(
						buildResponse(createStoredUserInfo(Role.ADMIN          , "registrationId", "userId1", 1)),
						buildResponse(createStoredUserInfo(Role.USER           , "registrationId", "userId2", 2)),
						buildResponse(createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "registrationId", "userId3", 3))
				)));
	}

	@Test @DirtiesContext
	void whenGetAllStoredUsers_isCalledByUser_returns403Forbidden() throws Exception {
		whenGetAllStoredUsers_isCalledByNotAllowedUser_returns403Forbidden(Role.USER, "userIdA");
	}
	@Test @DirtiesContext
	void whenGetAllStoredUsers_isCalledByUnknownAccount_returns403Forbidden() throws Exception {
		whenGetAllStoredUsers_isCalledByNotAllowedUser_returns403Forbidden(Role.UNKNOWN_ACCOUNT, "userIdB");
	}
	private void whenGetAllStoredUsers_isCalledByNotAllowedUser_returns403Forbidden(Role role, String userId) throws Exception {
		// Given
		fillStoredUserInfoRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users")
						.with(SecurityTestTools.buildUser(role, userId, "registrationId"+ userId, "login"))
				)

				// Then
				.andExpect(status().isForbidden())
				.andExpect(content().string(""));
	}

	@Test @DirtiesContext
	void whenGetAllStoredUsers_isCalledByUnauthenticated_returns401Unauthorized() throws Exception {
		// Given
		fillStoredUserInfoRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users")
				)

				// Then
				.andExpect(status().isUnauthorized())
				.andExpect(content().string(""));
	}

// ####################################################################################
//               StoredUserInfoController.updateStoredUser
// ####################################################################################

	@Test @DirtiesContext
	void updateStoredUser() {

	}

// ####################################################################################
//               StoredUserInfoController.deleteStoredUser
// ####################################################################################

// ####################################################################################
//               StoredUserInfoController.getDenialReasonForCurrentUser
// ####################################################################################

}