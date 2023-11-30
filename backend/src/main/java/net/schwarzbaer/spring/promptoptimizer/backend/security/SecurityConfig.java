package net.schwarzbaer.spring.promptoptimizer.backend.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.StoredUserInfoService;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final String initialAdmin;

	SecurityConfig(
			@Value("${app.security.initial-admin}") String initialAdmin
	) {
		this.initialAdmin = initialAdmin;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)

				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers( HttpMethod.GET,
								"/", "/index.html", "/vite.svg", "/assets/**",
								"/api/users/me", "/api/apistate"
						).permitAll()

						.requestMatchers( HttpMethod.GET,
								"/api/logout",
								"/api/users/reason"
						).authenticated()

						.requestMatchers(
								"/api/scenario/all",
								"/api/users",
								"/api/users/**"
						).hasRole(Role.ADMIN.getShort())

						.anyRequest().hasAnyRole(Role.ADMIN.getShort(), Role.USER.getShort())
				)

				.sessionManagement(sessions ->
						sessions.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

				.oauth2Login(withDefaults())

				.logout(logout -> logout
						.logoutUrl("/api/logout")
						.logoutSuccessHandler((request, response, authentication) ->
								response.setStatus(200)))

				.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
		;
		return http.build();
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(StoredUserInfoService storedUserInfoService) {
		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
		return request -> configureUserData(storedUserInfoService, delegate, request);
	}

	DefaultOAuth2User configureUserData(StoredUserInfoService storedUserInfoService, DefaultOAuth2UserService delegate, OAuth2UserRequest request) {
		OAuth2User user = delegate.loadUser(request);
		Collection<GrantedAuthority> newAuthorities = new ArrayList<>(user.getAuthorities());
		Map<String, Object> newAttributes = new HashMap<>(user.getAttributes());

		String registrationId = request.getClientRegistration().getRegistrationId();
		String userDbId = registrationId + user.getName();

		System.out.println("User: ["+ registrationId +"] "+ user.getName());
		newAttributes.forEach((key, value) ->
				System.out.println("   ["+key+"]: "+value+ (value==null ? "" : " { Class:"+value.getClass().getName()+" }"))
		);

		newAttributes.put(UserAttributesService.ATTR_USER_DB_ID, userDbId);
		newAttributes.put(UserAttributesService.ATTR_REGISTRATION_ID, registrationId);
		Role role = null;

		final Optional<StoredUserInfo> storedUserInfoOpt = storedUserInfoService.getUserById(userDbId);
		if (storedUserInfoOpt.isPresent()) {
			final StoredUserInfo storedUserInfo = storedUserInfoOpt.get();
			role = storedUserInfo.role();
			storedUserInfoService.updateUserIfNeeded(storedUserInfo, registrationId, newAttributes);
		}

		if (role==null && initialAdmin.equals(userDbId))
			role = Role.ADMIN;

		if (role==null)
			role = Role.UNKNOWN_ACCOUNT;

		if (storedUserInfoOpt.isEmpty())
			storedUserInfoService.addUser(userDbId, registrationId, role, newAttributes);

		newAuthorities.add(new SimpleGrantedAuthority(role.getLong()));
		return new DefaultOAuth2User(newAuthorities, newAttributes, UserAttributesService.ATTR_USER_DB_ID);
	}

}
