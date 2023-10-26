package net.schwarzbaer.spring.promptoptimizer.backend.security;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

public class SecurityTestTools {

	@NonNull
	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(@Nullable Role role) {
		return buildUser(role, "TestID", "TestLogin");
	}

	@NonNull
	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(@Nullable Role role, @NonNull String id, @NonNull String login) {
		return oidcLogin()
				.authorities(new SimpleGrantedAuthority(role == null ? "DummyAuthority" : role.getLong()))
				.userInfoToken(token -> token
						.claim("id", id)
						.claim("login", login)
				);
	}

	@NonNull
	public static MockHttpServletRequestBuilder buildGetCurrentUserRequest(@Nullable Role role, @NonNull String id, @NonNull String login) {
		return MockMvcRequestBuilders
				.get("/api/users/me")
				.with(buildUser(role, id, login));
	}

	@NonNull
	public static MockHttpServletRequestBuilder buildGetCurrentUserRequest() {
		return MockMvcRequestBuilders
				.get("/api/users/me");
	}

	public static class AllowedUserRoles implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Role.USER, Role.ADMIN).map(Arguments::of);
		}
	}
}