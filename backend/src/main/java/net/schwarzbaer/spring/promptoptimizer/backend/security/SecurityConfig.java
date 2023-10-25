package net.schwarzbaer.spring.promptoptimizer.backend.security;

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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public static final PrintStream DEBUG_OUT = System.out;
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

						.requestMatchers(
								"/api/logout"
						).authenticated()

						.requestMatchers(HttpMethod.GET,
								"/api/scenario/all",
								"/api/users/restricted"
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
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(/*WebClient rest*/) {
		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
		return request -> {

			OAuth2User user = delegate.loadUser(request);
			String userDbId = request.getClientRegistration().getRegistrationId() + user.getName();
			user.getAttributes().put("UserDbId", userDbId);
			/*
			query user database for role
			...
			If not found, do this
			 */
			if (initialAdmin.equals(userDbId)) {
//				DEBUG_OUT.println("########################################################");
//				DEBUG_OUT.println("    INITIAL ADMIN");
//				DEBUG_OUT.println("########################################################");

				Collection<GrantedAuthority> newAuthorities = new ArrayList<>(user.getAuthorities());
				newAuthorities.add(new SimpleGrantedAuthority(Role.ADMIN.getLong()));
				user = new DefaultOAuth2User(newAuthorities, user.getAttributes(), "id");
			}

			return user;
		};
	}

}
