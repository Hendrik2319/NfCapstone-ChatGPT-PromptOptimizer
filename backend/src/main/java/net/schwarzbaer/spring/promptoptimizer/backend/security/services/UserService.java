package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
//	@SuppressWarnings("java:S106")
//	private static final PrintStream DEBUG_OUT = System.out;

	private final UserRepository userRepository;

	public @NonNull UserInfos getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication!=null ? authentication.getPrincipal() : null;
//		if (principal!=null) DEBUG_OUT.println("Principal: "+principal.getClass()+" -> "+principal);

		if (principal instanceof OAuth2AuthenticatedPrincipal user) {
//			DEBUG_OUT.println("User Attributes:");
//			user.getAttributes().forEach((key, value) ->
//					DEBUG_OUT.println("   ["+key+"]: "+value+ (value==null ? "" : " { Class:"+value.getClass().getName()+" }"))
//			);

			return new UserInfos(
					true,
					hasRole(user, Role.USER),
					hasRole(user, Role.ADMIN),
					Objects.toString( user.getAttribute("id"), null ),
					Objects.toString( user.getAttribute("UserDbId"), null ),
					Objects.toString( user.getAttribute("login"), null ),
					Objects.toString( user.getAttribute("name"), null ),
					Objects.toString( user.getAttribute("location"), null ),
					Objects.toString( user.getAttribute("html_url"), null ),
					Objects.toString( user.getAttribute("avatar_url"), null )
			);
		}

		String name = authentication!=null ? authentication.getName() : "anonymousUser";
		return new UserInfos(false,false,false, name,null,null, null,null,null,null);
	}

	private boolean hasRole(OAuth2AuthenticatedPrincipal user, Role role) {
		for (GrantedAuthority authority : user.getAuthorities())
			if (authority.getAuthority().equals(role.getLong()))
				return true;
		return false;
	}

	public Optional<StoredUserInfo> getUserById(String userDbId) {
		return userRepository.findById(userDbId);
	}

	public void addUser(Role role, String registrationId, Map<String, Object> newAttributes) {
		// TODO
	}

	public void updateUserIfNeeded(StoredUserInfo storedUserInfo, Map<String, Object> newAttributes) {
		StoredUserInfo updatedUserInfo = updateUserObject(storedUserInfo, newAttributes);
		if (updatedUserInfo.equals(storedUserInfo))
			saveUser(updatedUserInfo);
	}

	private StoredUserInfo updateUserObject(StoredUserInfo storedUserInfo, Map<String, Object> newAttributes) {
		// TODO
		return storedUserInfo;
	}

	private void saveUser(StoredUserInfo updatedUserInfo) {
		// TODO
	}
}
