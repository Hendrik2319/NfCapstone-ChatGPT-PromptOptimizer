package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserServiceTest {

	@Mock private SecurityContext securityContext;
	@Mock private Authentication authentication;
	@InjectMocks private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
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

// ####################################################################################
//               getCurrentUser
// ####################################################################################

	@Test
	void whenGetCurrentUser_isCalledAndAuthenticationIsNull() {
		// Given
		when(securityContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(securityContext);

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
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
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
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
			String loggedUserID,
			String loggedUserLogin,
			boolean isUser, boolean isAdmin
	) {
		// Given
		initSecurityContext(loggedUserRole, loggedUserID, loggedUserLogin);

		// When
		UserInfos actual = userService.getCurrentUser();

		// Then
		UserInfos expected = new UserInfos(
				true, isUser, isAdmin,
				loggedUserID, null, loggedUserLogin, null, null, null, null
		);
		assertEquals(expected, actual);
	}
}