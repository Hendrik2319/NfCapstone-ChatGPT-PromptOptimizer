package net.schwarzbaer.spring.promptoptimizer.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

	private SecurityConfig securityConfig;
	private DefaultOAuth2UserService delegate;
	private OAuth2UserRequest oAuth2UserRequest;


	@BeforeEach
	void setup() {
		delegate = mock(DefaultOAuth2UserService.class);
		oAuth2UserRequest = mock(OAuth2UserRequest.class);
		securityConfig = new SecurityConfig("RegistrationId" + "InitialAdminID");

		ClientRegistration clientRegistration = mock(ClientRegistration.class);
		when(oAuth2UserRequest.getClientRegistration()).thenReturn(clientRegistration);
		when(clientRegistration.getRegistrationId()).thenReturn("RegistrationId");
	}

	@Test
	void whenConfigureUserData_isCalledByInitialAdmin() {
		//Given
		when(delegate.loadUser(oAuth2UserRequest)).thenReturn(new DefaultOAuth2User(
				List.of(),
				Map.of("id", "InitialAdminID"),
				"id"
		));

		//When
		DefaultOAuth2User actual = securityConfig.configureUserData(delegate, oAuth2UserRequest);

		//Then
		DefaultOAuth2User expected = new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority(Role.ADMIN.getLong())),
				Map.of(
						"id", "InitialAdminID",
						"UserDbId", "RegistrationId" + "InitialAdminID"
				),
				"id"
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenConfigureUserData_isCalledByAnotherUser() {
		//Given
		when(delegate.loadUser(oAuth2UserRequest)).thenReturn(new DefaultOAuth2User(
				List.of(),
				Map.of("id", "UserID"),
				"id"
		));

		//When
		DefaultOAuth2User actual = securityConfig.configureUserData(delegate, oAuth2UserRequest);

		//Then
		DefaultOAuth2User expected = new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority(Role.UNKNOWN_ACCOUNT.getLong())),
				Map.of(
						"id", "UserID",
						"UserDbId", "RegistrationId" + "UserID"
				),
				"id"
		);
		assertEquals(expected, actual);
	}
}