package net.schwarzbaer.spring.promptoptimizer.backend.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService;

public class SecurityTestTools {

	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(
		@Nullable Role role,
		@NonNull UserAttributesService.Registration registration
	) {
		return buildUser(role, "TestID", registration, "TestLogin");
	}

	public static SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor buildUser(
		@Nullable Role role,
		@NonNull String id,
		@NonNull UserAttributesService.Registration registration,
		@NonNull String login
	) {
		return oidcLogin()
				.authorities(new SimpleGrantedAuthority(role == null ? "DummyAuthority" : role.getLong()))
				.userInfoToken(token -> {
					switch (registration) {
						case GITHUB:
							token.claim("id", id);
							token.claim("login", login);
							break;
						case GOOGLE:
							token.claim("original_Id", id);
							token.claim("email", login);
							break;
					}
					token.claim(UserAttributesService.ATTR_USER_DB_ID, getUserDbId(id, registration));
					token.claim(UserAttributesService.ATTR_REGISTRATION_ID, registration.id);
				});
	}

	public static @NonNull String getUserDbId(@NonNull String userId, @NonNull UserAttributesService.Registration registration) {
		return registration.id + userId;
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
