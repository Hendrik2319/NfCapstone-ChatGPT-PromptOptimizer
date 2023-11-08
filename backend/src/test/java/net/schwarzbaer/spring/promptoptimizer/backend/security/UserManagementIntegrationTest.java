package net.schwarzbaer.spring.promptoptimizer.backend.security;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.StoredUserInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

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
//               Tools to handle StoredUserInfoRepository
// ####################################################################################

	@NonNull
	private static StoredUserInfo createStoredUserInfo(Role role, String originalId, int index) {
		return new StoredUserInfo(
				"registrationId" + originalId, role, "registrationId", originalId,
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
		addValue(valueStrs, storedUserInfo.url           (), "url"           , "\"%s\"");
		addValue(valueStrs, storedUserInfo.avatar_url    (), "avatar_url"    , "\"%s\"");
		addValue(valueStrs, storedUserInfo.denialReason  (), "denialReason"  , "\"%s\"");
		return "{ %s }".formatted( String.join(", ", valueStrs) );
	}

	private void addValue(List<String> valueStrs, Object value, String valueName, String format) {
		if (value!=null)
			valueStrs.add( ("\"%s\": "+format).formatted(valueName, value) );
	}

	private void fillStoredUserInfoRepository() {
		storedUserInfoRepository.save(createStoredUserInfo(Role.ADMIN          , "userId1", 1));
		storedUserInfoRepository.save(createStoredUserInfo(Role.USER           , "userId2", 2));
		storedUserInfoRepository.save(createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "userId3", 3));
	}
	private void assertStoredUserInfoRepositoryUnchanged() {
		asserEntryEquals("registrationIduserId1", createStoredUserInfo(Role.ADMIN          , "userId1", 1));
		asserEntryEquals("registrationIduserId2", createStoredUserInfo(Role.USER           , "userId2", 2));
		asserEntryEquals("registrationIduserId3", createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "userId3", 3));
	}
	private void assertStoredUserInfoRepositoryHasOneChange(String originalId, StoredUserInfo expected) {
		asserEntryEquals("registrationIduserId1", "userId1".equals(originalId) ? expected : createStoredUserInfo(Role.ADMIN          , "userId1", 1));
		asserEntryEquals("registrationIduserId2", "userId2".equals(originalId) ? expected : createStoredUserInfo(Role.USER           , "userId2", 2));
		asserEntryEquals("registrationIduserId3", "userId3".equals(originalId) ? expected : createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "userId3", 3));
	}
	private void asserEntryEquals(String id, StoredUserInfo expected) {
		Optional<StoredUserInfo> actualOpt = storedUserInfoRepository.findById(id);
		assertNotNull(actualOpt);
		if (expected==null)
			assertTrue(actualOpt.isEmpty());
		else
		{
			assertTrue(actualOpt.isPresent());
			assertEquals(expected, actualOpt.get());
		}
	}

	private void performRequest_return400BadRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		// Given
		fillStoredUserInfoRepository();
		// When
		mockMvc.perform( requestBuilder )
				// Then
				.andExpect(status().isBadRequest());
		assertStoredUserInfoRepositoryUnchanged();
	}

	private void performRequest_return404NotFound(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		// Given
		fillStoredUserInfoRepository();
		// When
		mockMvc.perform(requestBuilder)
				// Then
				.andExpect(status().isNotFound());
		assertStoredUserInfoRepositoryUnchanged();
	}

	private void performRequest_return404NotFoundAndNoText(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		// Given
		fillStoredUserInfoRepository();
		// When
		mockMvc.perform(requestBuilder)
				// Then
				.andExpect(status().isNotFound())
				.andExpect(content().string(""));
		assertStoredUserInfoRepositoryUnchanged();
	}

	private void performRequest_return403Forbidden(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		// Given
		fillStoredUserInfoRepository();
		// When
		mockMvc.perform( requestBuilder )
				// Then
				.andExpect(status().isForbidden())
				.andExpect(content().string(""));
		assertStoredUserInfoRepositoryUnchanged();
	}

	private void performRequest_return401Unauthorized(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		// Given
		fillStoredUserInfoRepository();
		// When
		mockMvc.perform( requestBuilder )
				// Then
				.andExpect(status().isUnauthorized())
				.andExpect(content().string(""));
		assertStoredUserInfoRepositoryUnchanged();
	}

// ####################################################################################
//               StoredUserInfoController.getAllStoredUsers
// ####################################################################################

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
						buildResponse(createStoredUserInfo(Role.ADMIN          , "userId1", 1)),
						buildResponse(createStoredUserInfo(Role.USER           , "userId2", 2)),
						buildResponse(createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "userId3", 3))
				)));

		assertStoredUserInfoRepositoryUnchanged();
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
		performRequest_return403Forbidden(MockMvcRequestBuilders
				.get("/api/users")
				.with(SecurityTestTools.buildUser(role, userId, "registrationId" + userId, "login")));
	}

	@Test @DirtiesContext
	void whenGetAllStoredUsers_isCalledByUnauthenticated_returns401Unauthorized() throws Exception {
		// Given
		performRequest_return401Unauthorized(MockMvcRequestBuilders
				.get("/api/users"));
	}

// ####################################################################################
//               StoredUserInfoController.updateStoredUser
// ####################################################################################

	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledNormal_returnsUpdatedData() throws Exception {
		// Given
		fillStoredUserInfoRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.put("/api/users/%s".formatted("registrationIduserId2"))
						.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "registrationIduserId1", "login"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(
								buildResponse(createStoredUserInfo(Role.USER, "userId2", 7))
						)
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().json(
						buildResponse(createStoredUserInfo(Role.USER, "userId2", 7))
				));

		assertStoredUserInfoRepositoryHasOneChange("userId2",
				createStoredUserInfo(Role.USER, "userId2", 7)
		);
	}

	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledWithUnknownId_returns404NotFound() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/users/%s".formatted("registrationIduserId7"))
				.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "registrationIduserId1", "login"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						buildResponse(createStoredUserInfo(Role.USER, "userId7", 7))
				);
		performRequest_return404NotFoundAndNoText(requestBuilder);
	}

	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledByUnknownAccount_returns403Forbidden() throws Exception {
		whenUpdateStoredUser_isCalledByNotAllowedUser_returns403Forbidden(Role.UNKNOWN_ACCOUNT);
	}
	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledByUSER_returns403Forbidden() throws Exception {
		whenUpdateStoredUser_isCalledByNotAllowedUser_returns403Forbidden(Role.USER);
	}
	private void whenUpdateStoredUser_isCalledByNotAllowedUser_returns403Forbidden(Role role) throws Exception {
		performRequest_return403Forbidden( MockMvcRequestBuilders
				.put("/api/users/%s".formatted("registrationIduserId2"))
				.with(SecurityTestTools.buildUser(role, "userId1", "registrationIduserId1", "login"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						buildResponse(createStoredUserInfo(Role.USER, "userId2", 7))
				)
		);
	}

	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledByUnauthenticated_returns401Unauthorized() throws Exception {
		performRequest_return401Unauthorized( MockMvcRequestBuilders
				.put("/api/users/%s".formatted("registrationIduserId2"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						buildResponse(createStoredUserInfo(Role.USER, "userId2", 7))
				)
		);
	}

	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledWithNoIdInData_returns400BadRequest() throws Exception {
		whenUpdateStoredUser_isCalledWithWrongIDs_returns400BadRequest("userId2", null);
	}
	@Test @DirtiesContext
	void whenUpdateStoredUser_isCalledWithDifferentIDs_returns400BadRequest() throws Exception {
		whenUpdateStoredUser_isCalledWithWrongIDs_returns400BadRequest("userId3", "userId2");
	}
	private void whenUpdateStoredUser_isCalledWithWrongIDs_returns400BadRequest(String idInPath, String idInData) throws Exception {
		performRequest_return400BadRequest( MockMvcRequestBuilders
				.put("/api/users/%s".formatted("registrationId" + idInPath))
				.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "registrationIduserId1", "login"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						buildResponse(createStoredUserInfo(Role.USER, idInData, 7))
				)
		);
	}

// ####################################################################################
//               StoredUserInfoController.deleteStoredUser
// ####################################################################################

	@Test @DirtiesContext
	void whenDeleteStoredUser_isCalledNormal() throws Exception {
		// Given
		fillStoredUserInfoRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.delete("/api/users/%s".formatted("registrationIduserId3"))
						.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "registrationIduserId1", "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		assertStoredUserInfoRepositoryHasOneChange("userId3", null);
	}

	@Test @DirtiesContext
	void whenDeleteStoredUser_isCalledWithUnknownId_returns404NotFound() throws Exception {
		performRequest_return404NotFound( MockMvcRequestBuilders
				.delete("/api/users/%s".formatted("registrationIduserId7"))
				.with(SecurityTestTools.buildUser(Role.ADMIN, "userId1", "registrationIduserId1", "login"))
		);
	}

	@Test @DirtiesContext
	void whenDeleteStoredUser_isCalledByUser_returns403Forbidden() throws Exception {
		whenDeleteStoredUser_isCalledByNotAllowedUser_returns403Forbidden(Role.USER, "userIdA");
	}
	@Test @DirtiesContext
	void whenDeleteStoredUser_isCalledByUnknownAccount_returns403Forbidden() throws Exception {
		whenDeleteStoredUser_isCalledByNotAllowedUser_returns403Forbidden(Role.UNKNOWN_ACCOUNT, "userIdB");
	}
	private void whenDeleteStoredUser_isCalledByNotAllowedUser_returns403Forbidden(Role role, String userId) throws Exception {
		performRequest_return403Forbidden( MockMvcRequestBuilders
				.delete("/api/users/%s".formatted("registrationIduserId2"))
				.with(SecurityTestTools.buildUser(role, userId, "registrationId" + userId, "login"))
		);
	}

	@Test @DirtiesContext
	void whenDeleteStoredUser_isCalledByUnauthenticated_returns401Unauthorized() throws Exception {
		performRequest_return401Unauthorized( MockMvcRequestBuilders
				.delete("/api/users/%s".formatted("registrationIduserId2"))
		);
	}

// ####################################################################################
//               StoredUserInfoController.getDenialReasonForCurrentUser
// ####################################################################################

	@Test @DirtiesContext
	void whenGetDenialReasonForCurrentUser_isCalledByADMIN_returnsString() throws Exception {
		whenGetDenialReasonForCurrentUser_isCalledNormal_returnsString(Role.ADMIN, "userId1", "reason1");
	}
	@Test @DirtiesContext
	void whenGetDenialReasonForCurrentUser_isCalledUSER_returnsString() throws Exception {
		whenGetDenialReasonForCurrentUser_isCalledNormal_returnsString(Role.USER, "userId2", "reason2");
	}
	@Test @DirtiesContext
	void whenGetDenialReasonForCurrentUser_isCalledUnknownAccount_returnsString() throws Exception {
		whenGetDenialReasonForCurrentUser_isCalledNormal_returnsString(Role.UNKNOWN_ACCOUNT, "userId3", "reason3");
	}
	@Test @DirtiesContext
	void whenGetDenialReasonForCurrentUser_isCalledWithUnknownUserId_returnsEmptyString() throws Exception { // --> no reason --> "Please wait, until ..."
		whenGetDenialReasonForCurrentUser_isCalledNormal_returnsString(Role.UNKNOWN_ACCOUNT, "userId4", "");
	}
	private void whenGetDenialReasonForCurrentUser_isCalledNormal_returnsString(Role role, String originalId, String expectedReason) throws Exception {
		// Given
		fillStoredUserInfoRepository();

		// When
		mockMvc
				.perform(MockMvcRequestBuilders
						.get("/api/users/reason")
						.with(SecurityTestTools.buildUser(role, originalId, "registrationId"+ originalId, "login"))
				)

				// Then
				.andExpect(status().isOk())
				.andExpect(content().string(expectedReason));

		assertStoredUserInfoRepositoryUnchanged();
	}

	@Test @DirtiesContext
	void whenGetDenialReasonForCurrentUser_isCalledByUnauthenticated_returns401Unauthorized() throws Exception {
		performRequest_return401Unauthorized( MockMvcRequestBuilders
				.get("/api/users/reason")
		);
	}

}