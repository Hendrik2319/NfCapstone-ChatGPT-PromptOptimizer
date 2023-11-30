package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfo;

class UserServiceTest {

	@Mock private SecurityContext securityContext;
	@Mock private Authentication authentication;
	@Mock private UserAttributesService userAttributesService;
	@InjectMocks private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@NonNull
	private static DefaultOAuth2User buildUser(@Nullable Role role, @NonNull String id, @NonNull String registrationId, @NonNull String login) {
		return new DefaultOAuth2User(
				role==null
						? List.of()
						: List.of( new SimpleGrantedAuthority( role.getLong() ) ),
				Map.of(
						"originalId", id,
						UserAttributesService.ATTR_USER_DB_ID, registrationId + id,
						UserAttributesService.ATTR_REGISTRATION_ID, registrationId,
						"login", login
				),
				UserAttributesService.ATTR_USER_DB_ID);
	}

	private void initSecurityContext(@Nullable Role role, @NonNull String id, @NonNull String registrationId, @NonNull String login) {
		DefaultOAuth2User user = buildUser(role, id, registrationId, login);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(user);
		when(authentication.getName()).thenThrow(IllegalStateException.class);

		when(userAttributesService.getAttribute(user, UserAttributesService.ATTR_REGISTRATION_ID, null)).thenReturn(registrationId);
		when(userAttributesService.getAttribute(user, UserAttributesService.ATTR_USER_DB_ID     , null)).thenReturn(registrationId + id);
		when(userAttributesService.getAttribute(user, registrationId, UserAttributesService.Field.ORIGINAL_ID, null)).thenReturn(id);
		when(userAttributesService.getAttribute(user, registrationId, UserAttributesService.Field.LOGIN      , null)).thenReturn(login);

		SecurityContextHolder.setContext(securityContext);
	}

// ####################################################################################
//               getCurrentUser
// ####################################################################################

	@Test
	void whenGetCurrentUser_isCalledAndAuthenticationIsNull() {
		// Given
		when(securityContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);

		// When
		UserInfo actual = userService.getCurrentUser();

		// Then
		UserInfo expected = new UserInfo(
				false, false, false,
				"anonymousUser", null, null, null, null, null, null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetCurrentUser_isCalledByAnonymous() {
		// Given
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn("anonymousUser");
		when(authentication.getName()).thenReturn("anonymousUser");
		SecurityContextHolder.setContext(securityContext);

		// When
		UserInfo actual = userService.getCurrentUser();

		// Then
		UserInfo expected = new UserInfo(
				false, false, false,
				"anonymousUser", null, null, null, null, null, null
		);
		assertEquals(expected, actual);
	}

	@Test void whenGetCurrentUser_isCalledByAuthenticatedUser() {
		whenGetCurrentUser_isCalled(
				null, "TestID2", "TestLogin",
				false, false );
	}
	@Test void whenGetCurrentUser_isCalledByUnknownAccount() {
		whenGetCurrentUser_isCalled(
				Role.UNKNOWN_ACCOUNT, "TestID3", "TestUnknownAccount",
				false, false );
	}
	@Test void whenGetCurrentUser_isCalledByUser() {
		whenGetCurrentUser_isCalled(
				Role.USER, "TestID4", "TestUser",
				true, false );
	}
	@Test void whenGetCurrentUser_isCalledByAdmin() {
		whenGetCurrentUser_isCalled(
				Role.ADMIN, "TestID5", "TestAdmin",
				false, true );
	}
	private void whenGetCurrentUser_isCalled(
			Role loggedUserRole,
			@NonNull String loggedUserID,
			@NonNull String loggedUserLogin,
			boolean isUser, boolean isAdmin
	) {
		// Given
		initSecurityContext(loggedUserRole, loggedUserID, "Registration1", loggedUserLogin);

		// When
		UserInfo actual = userService.getCurrentUser();

		// Then
		UserInfo expected = new UserInfo(
				true, isUser, isAdmin,
				loggedUserID, "Registration1"+loggedUserID, loggedUserLogin, null, null, null, null
		);
		assertEquals(expected, actual);
	}
}