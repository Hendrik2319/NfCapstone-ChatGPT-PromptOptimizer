package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserAttributes;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfo;

@Service
@RequiredArgsConstructor
public class UserService {

// ####################################################################################
//               Called by and allowed for all users (authorized or not)
// ####################################################################################

	public @NonNull UserInfo getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication!=null ? authentication.getPrincipal() : null;

		if (principal instanceof OAuth2AuthenticatedPrincipal user) {
			String registrationId = UserAttributes.getAttribute( user, UserAttributes.ATTR_REGISTRATION_ID, null );

			return new UserInfo(
					true,
					hasRole(user, Role.USER),
					hasRole(user, Role.ADMIN),
					UserAttributes.getAttribute( user, registrationId, UserAttributes.Field.ORIGINAL_ID, null ),
					UserAttributes.getAttribute( user, UserAttributes.ATTR_USER_DB_ID, null ),
					UserAttributes.getAttribute( user, registrationId, UserAttributes.Field.LOGIN      , null ),
					UserAttributes.getAttribute( user, registrationId, UserAttributes.Field.NAME       , null ),
					UserAttributes.getAttribute( user, registrationId, UserAttributes.Field.LOCATION   , null ),
					UserAttributes.getAttribute( user, registrationId, UserAttributes.Field.URL        , null ),
					UserAttributes.getAttribute( user, registrationId, UserAttributes.Field.AVATAR_URL , null )
			);
		}

		String name = authentication!=null ? authentication.getName() : "anonymousUser";
		return new UserInfo(false,false,false, name,null,null, null,null,null,null);
	}

	private boolean hasRole(OAuth2AuthenticatedPrincipal user, Role role) {
		for (GrantedAuthority authority : user.getAuthorities())
			if (authority.getAuthority().equals(role.getLong()))
				return true;
		return false;
	}
}
