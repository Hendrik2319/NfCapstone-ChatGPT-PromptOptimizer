package net.schwarzbaer.spring.promptoptimizer.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

	private SecurityContext securityContext;
	private Authentication authentication;
	private UserService userService;

	@BeforeEach
	void setUp() {
		securityContext = mock(SecurityContext.class);
		authentication = mock(Authentication.class);
		userService = new UserService();
	}

	@NonNull
	public static DefaultOAuth2User buildUser(@Nullable Role role, @NonNull String id, @NonNull String login) {
		return new DefaultOAuth2User(
				role==null
						? List.of()
						: List.of( new SimpleGrantedAuthority( role.getLong() ) ),
				Map.of(
						"id", id,
						"login", login
				),
				"id");
	}

	private void initSecurityContext(@Nullable Role role, @NonNull String id, @NonNull String login) {
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(buildUser(role, id, login));
		when(authentication.getName()).thenThrow(IllegalStateException.class);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void whenGetCurrentUser_isCalledByAnonymous() {
		// Given
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn("anonymousUser");
		when(authentication.getName()).thenReturn("anonymousUser");
		SecurityContextHolder.setContext(securityContext);

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
				false, false, false,
				"anonymousUser", null, null, null, null, null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetCurrentUser_isCalledByAuthenticatedUser() {
		// Given
		initSecurityContext(null, "TestID2", "TestLogin");

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
				true, false, false,
				"TestID2", "TestLogin", null, null, null, null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetCurrentUser_isCalledByUnknownAccount() {
		// Given
		initSecurityContext(Role.UNKNOWN_ACCOUNT, "TestID3", "TestUnknownAccount");

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
				true, false, false,
				"TestID3", "TestUnknownAccount", null, null, null, null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetCurrentUser_isCalledByUser() {
		// Given
		initSecurityContext(Role.USER, "TestID4", "TestUser");

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
				true, true, false,
				"TestID4", "TestUser", null, null, null, null
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetCurrentUser_isCalledByAdmin() {
		// Given
		initSecurityContext(Role.ADMIN, "TestID5", "TestAdmin");

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
				true, false, true,
				"TestID5", "TestAdmin", null, null, null, null
		);
		assertEquals(expected, actual);
	}
}