package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

	private final UserRepository userRepository;

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

// ####################################################################################
//               Called by SecurityConfig
// ####################################################################################

	public Optional<StoredUserInfo> getUserById(String userDbId) {
		return userRepository.findById(userDbId);
	}

	public void addUser(Role role, String registrationId, Map<String, Object> newAttributes) {
		userRepository.save(new StoredUserInfo(
				Objects.toString(newAttributes.get(ATTR_USER_DB_ID ), null),
				role,
				registrationId,
				Objects.toString(newAttributes.get(ATTR_ORIGINAL_ID), null),
				Objects.toString(newAttributes.get(ATTR_LOGIN      ), null),
				Objects.toString(newAttributes.get(ATTR_NAME       ), null),
				Objects.toString(newAttributes.get(ATTR_LOCATION   ), null),
				Objects.toString(newAttributes.get(ATTR_URL        ), null),
				Objects.toString(newAttributes.get(ATTR_AVATAR_URL ), null)
		));
	}

	public void updateUserIfNeeded(StoredUserInfo storedUserInfo, Map<String, Object> newAttributes) {
		StoredUserInfo updatedUserInfo = new StoredUserInfo(
				storedUserInfo.id(),
				storedUserInfo.role(),
				storedUserInfo.registrationId(),
				Objects.toString(newAttributes.get(ATTR_ORIGINAL_ID), storedUserInfo.originalId()),
				Objects.toString(newAttributes.get(ATTR_LOGIN      ), storedUserInfo.login     ()),
				Objects.toString(newAttributes.get(ATTR_NAME       ), storedUserInfo.name      ()),
				Objects.toString(newAttributes.get(ATTR_LOCATION   ), storedUserInfo.location  ()),
				Objects.toString(newAttributes.get(ATTR_URL        ), storedUserInfo.url       ()),
				Objects.toString(newAttributes.get(ATTR_AVATAR_URL ), storedUserInfo.avatar_url())
		);
		if (!updatedUserInfo.equals(storedUserInfo))
			userRepository.save(updatedUserInfo);
	}

// ####################################################################################
//               Called by and allowed for Admin
// ####################################################################################

	public List<StoredUserInfo> getAllStoredUsers()
			throws UserIsNotAllowedException
	{
		// TODO
		return null;
	}

	public Optional<StoredUserInfo> updateStoredUser(@NonNull String id, @NonNull StoredUserInfo storedUserInfo)
			throws UserIsNotAllowedException
	{
		// TODO
		return null;
	}

	public void deleteStoredUser(@NonNull String id)
			throws UserIsNotAllowedException
	{
		// TODO
	}

// ####################################################################################
//               Called by and allowed for authorized users
// ####################################################################################

	public String getDenialReason(String id) {
		// TODO
		return null;
	}
}
