package net.schwarzbaer.spring.promptoptimizer.backend.security;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

public class SecurityTestTools {

	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(@Nullable Role role) {
		return buildUser(role, "TestID", null, "TestLogin");
	}

	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(@Nullable Role role, @NonNull String id, @NonNull String login) {
		return buildUser(role, id, null, login);
	}

	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(@Nullable Role role, @NonNull String id, @Nullable String userDbId, @NonNull String login) {
		return oidcLogin()
				.authorities(new SimpleGrantedAuthority(role == null ? "DummyAuthority" : role.getLong()))
				.userInfoToken(token -> {
					token.claim("id", id);
					token.claim("login", login);
					if (userDbId!=null) token.claim("UserDbId", userDbId);
				});
	}

	public static class UserAndAdminRoles implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Role.USER, Role.ADMIN).map(Arguments::of);
		}
	}

	public static class NotAdminRoles implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Role.USER, Role.UNKNOWN_ACCOUNT).map(Arguments::of);
		}
	}

	public static class AllRoles implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Role.values()).map(Arguments::of);
		}
	}
}
