package net.schwarzbaer.spring.promptoptimizer.backend.security;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.StoredUserInfoService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

	private SecurityConfig securityConfig;
	@Mock private DefaultOAuth2UserService delegate;
	@Mock private OAuth2UserRequest oAuth2UserRequest;
	@Mock private StoredUserInfoService storedUserInfoService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		securityConfig = new SecurityConfig("RegistrationId" + "InitialAdminID");

		ClientRegistration clientRegistration = mock(ClientRegistration.class);
		when(oAuth2UserRequest.getClientRegistration()).thenReturn(clientRegistration);
		when(clientRegistration.getRegistrationId()).thenReturn("RegistrationId");
	}

	@Test void whenConfigureUserData_isCalledWithEmptyDbByInitialAdmin() {
		whenConfigureUserData_isCalledWithEmptyDb("InitialAdminID", Role.ADMIN);
	}
	@Test void whenConfigureUserData_isCalledWithEmptyDbByAnotherUser() {
		whenConfigureUserData_isCalledWithEmptyDb("UserID", Role.UNKNOWN_ACCOUNT);
	}
	private void whenConfigureUserData_isCalledWithEmptyDb(String userID, @NonNull Role expectedRole) {
		//Given
		when(delegate.loadUser(oAuth2UserRequest)).thenReturn(new DefaultOAuth2User(
				List.of(), Map.of("id", userID), "id"
		));
		when(storedUserInfoService.getUserById("RegistrationId" + userID)).thenReturn(
				Optional.empty()
		);

		//When
		DefaultOAuth2User actual = securityConfig.configureUserData(storedUserInfoService, delegate, oAuth2UserRequest);

		//Then
		Map<String, Object> newAttributes = Objects.requireNonNull( Map.of(
				"id", userID,
				"UserDbId", "RegistrationId" + userID
		) );
		verify(storedUserInfoService).getUserById("RegistrationId" + userID);
		verify(storedUserInfoService, times(0)).updateUserIfNeeded(any(),any(),any());
		verify(storedUserInfoService).addUser("RegistrationId" + userID, "RegistrationId", expectedRole, newAttributes);
		DefaultOAuth2User expected = new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority(expectedRole.getLong())),
				newAttributes,
				"id"
		);
		assertEquals(expected, actual);
	}

	@ParameterizedTest
	@ArgumentsSource(SecurityTestTools.AllRoles.class)
	void whenConfigureUserData_isCalledStoredUser(Role expectedRole) {
		//Given
		when(delegate.loadUser(oAuth2UserRequest)).thenReturn(new DefaultOAuth2User(
				List.of(), Map.of("id", "userID"), "id"
		));
		when(storedUserInfoService.getUserById("RegistrationId" + "userID")).thenReturn(
				Optional.of(createStoredUserInfo(expectedRole))
		);

		//When
		DefaultOAuth2User actual = securityConfig.configureUserData(storedUserInfoService, delegate, oAuth2UserRequest);

		//Then
		Map<String, Object> newAttributes = Map.of(
				"id", "userID",
				"UserDbId", "RegistrationId" + "userID"
		);
		verify(storedUserInfoService).getUserById("RegistrationId" + "userID");
		verify(storedUserInfoService).updateUserIfNeeded(
				createStoredUserInfo(expectedRole),
				"RegistrationId",
				newAttributes
		);
		verify(storedUserInfoService, times(0)).addUser(any(),any(),any(),any());
		DefaultOAuth2User expected = new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority(expectedRole.getLong())),
				newAttributes,
				"id"
		);
		assertEquals(expected, actual);
	}

	@NotNull
	private static StoredUserInfo createStoredUserInfo(Role role) {
		return new StoredUserInfo(
				"RegistrationIduserID", role, "RegistrationId", "userID",
				"login", null, null, null, null, null
		);
	}
}