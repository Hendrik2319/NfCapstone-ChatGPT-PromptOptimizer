package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

	static final String ATTR_ORIGINAL_ID = "id";
	static final String ATTR_USER_DB_ID  = "UserDbId";
	static final String ATTR_LOGIN       = "login";
	static final String ATTR_NAME        = "name";
	static final String ATTR_LOCATION    = "location";
	static final String ATTR_URL         = "html_url";
	static final String ATTR_AVATAR_URL  = "avatar_url";

	//	@SuppressWarnings("java:S106")
	//	private static final PrintStream DEBUG_OUT = System.out;

// ####################################################################################
//               Called by and allowed for all users (authorized or not)
// ####################################################################################

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
					Objects.toString( user.getAttribute(ATTR_ORIGINAL_ID), null ),
					Objects.toString( user.getAttribute(ATTR_USER_DB_ID ), null ),
					Objects.toString( user.getAttribute(ATTR_LOGIN      ), null ),
					Objects.toString( user.getAttribute(ATTR_NAME       ), null ),
					Objects.toString( user.getAttribute(ATTR_LOCATION   ), null ),
					Objects.toString( user.getAttribute(ATTR_URL        ), null ),
					Objects.toString( user.getAttribute(ATTR_AVATAR_URL ), null )
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
}