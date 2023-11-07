package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

	@Mock private SecurityContext securityContext;
	@Mock private Authentication authentication;
	@Mock private UserRepository userRepository;
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

// ####################################################################################
//               getUserById
// ####################################################################################

	@Test
	void whenGetUserById_getsUnknownId_returnsEmptyOptional() {
		// Given
		when(userRepository.findById(any())).thenReturn(
				Optional.empty()
		);

		// When
		Optional<StoredUserInfo> actual = userService.getUserById("userDbId");

		// Then
		verify(userRepository).findById("userDbId");
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test
	void whenGetUserById_getsKnownId_returnsOptionalWithData() {
		// Given
		when(userRepository.findById(any())).thenReturn(
				Optional.of(new StoredUserInfo(
						"RegistrationIduserID", Role.USER, "RegistrationId",
						"userID", "login",
						null, null, null, null
				))
		);

		// When
		Optional<StoredUserInfo> actual = userService.getUserById("RegistrationIduserID");

		// Then
		verify(userRepository).findById("RegistrationIduserID");
		StoredUserInfo expected = new StoredUserInfo(
				"RegistrationIduserID", Role.USER, "RegistrationId",
				"userID", "login",
				null, null, null, null
		);
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		assertEquals(expected, actual.get());
	}

// ####################################################################################
//               addUser
// ####################################################################################

	@Test
	void whenAddUser_isCalled() {
		// Given
		// When
		Map<String, Object> attrs = Map.of(
				"UserDbId"  , "RegistrationIDuserID1",
				"id"        , "userID1"  ,
				"login"     , "login1"   ,
				"name"      , "name1"    ,
				"location"  , "location1",
				"html_url"  , "url1"     ,
				"avatar_url", "avatarUrl1"
		);
		userService.addUser(Role.UNKNOWN_ACCOUNT, "RegistrationID", attrs);

		// Then
		verify(userRepository).save(new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1"
		));
	}

// ####################################################################################
//               updateUserIfNeeded
// ####################################################################################

	@Test
	void whenUpdateUserIfNeeded_isCalledWithNewData() {
		// Given
		// When
		Map<String, Object> attrs = Map.of(
				"UserDbId"  , "RegistrationIDuserID1",
				"id"        , "userID1"  ,
				"login"     , "login1"   ,
				"name"      , "name1"    ,
				"location"  , "location1",
				"html_url"  , "url1"     ,
				"avatar_url", "avatarUrl1"
		);
		StoredUserInfo storedUserInfo = new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login2",
				"name2", "location2", "url2", "avatarUrl2"
		);
		userService.updateUserIfNeeded(storedUserInfo, attrs);

		// Then
		verify(userRepository).save(new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1"
		));
	}

	@Test void whenUpdateUserIfNeeded_isCalledWithNoData() {
		whenUpdateUserIfNeeded_isCalled_andNothingIsWrittenToRepo(Map.of());
	}
	@Test void whenUpdateUserIfNeeded_isCalledWithSameData() {
		whenUpdateUserIfNeeded_isCalled_andNothingIsWrittenToRepo(Map.of(
				"UserDbId"  , "RegistrationIDuserID1",
				"id"        , "userID1"  ,
				"login"     , "login1"   ,
				"name"      , "name1"    ,
				"location"  , "location1",
				"html_url"  , "url1"     ,
				"avatar_url", "avatarUrl1"
		));
	}
	private void whenUpdateUserIfNeeded_isCalled_andNothingIsWrittenToRepo(Map<String, Object> attrs) {
		// Given

		// When
		StoredUserInfo storedUserInfo = new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1"
		);
		userService.updateUserIfNeeded(storedUserInfo, attrs);

		// Then
		verify(userRepository, times(0)).save(any());
	}
}