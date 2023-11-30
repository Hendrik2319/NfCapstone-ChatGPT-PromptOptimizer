package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserAttributes;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.StoredUserInfoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StoredUserInfoService {

	private final StoredUserInfoRepository storedUserInfoRepository;
	private final UserService userService;

// ####################################################################################
//               Called by SecurityConfig
// ####################################################################################

	public Optional<StoredUserInfo> getUserById(String userDbId) {
		return storedUserInfoRepository.findById(userDbId);
	}

	public void addUser(String userDbId, String registrationId, Role role, Map<String, Object> newAttributes) {
		userDbId = Objects.requireNonNull(userDbId);
		role = Objects.requireNonNull(role);
		newAttributes = Objects.requireNonNull(newAttributes);

		storedUserInfoRepository.save(new StoredUserInfo(
				userDbId,
				role,
				registrationId,
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.ORIGINAL_ID, null ),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.LOGIN      , null ),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.NAME       , null ),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.LOCATION   , null ),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.URL        , null ),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.AVATAR_URL , null ),
				null
		));
	}

	public void updateUserIfNeeded(StoredUserInfo storedUserInfo, String registrationId, Map<String, Object> newAttributes) {
		storedUserInfo = Objects.requireNonNull(storedUserInfo);
		newAttributes = Objects.requireNonNull(newAttributes);

		StoredUserInfo updatedUserInfo = new StoredUserInfo(
				storedUserInfo.id(),
				storedUserInfo.role(),
				storedUserInfo.registrationId(),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.ORIGINAL_ID, storedUserInfo.originalId()),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.LOGIN      , storedUserInfo.login     ()),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.NAME       , storedUserInfo.name      ()),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.LOCATION   , storedUserInfo.location  ()),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.URL        , storedUserInfo.url       ()),
				UserAttributes.getAttribute( newAttributes, registrationId, UserAttributes.Field.AVATAR_URL , storedUserInfo.avatar_url()),
				storedUserInfo.denialReason()
		);
		
		if (registrationId!=null && !updatedUserInfo.equals(storedUserInfo))
			storedUserInfoRepository.save(updatedUserInfo);
	}

// ####################################################################################
//               Called by and allowed for Admin
// ####################################################################################

	public List<StoredUserInfo> getAllStoredUsers()
			throws UserIsNotAllowedException
	{
		UserInfo currentUser = userService.getCurrentUser();
		if (!currentUser.isAdmin())
			 throw new UserIsNotAllowedException("Current user is not allowed to get all stored users.");

		return storedUserInfoRepository.findAll();
	}

	public Optional<StoredUserInfo> updateStoredUser(@NonNull String id, @NonNull StoredUserInfo storedUserInfo)
			throws UserIsNotAllowedException
	{
		if ( storedUserInfo.id()==null     ) throw new IllegalArgumentException("StoredUserInfo have no [id]");
		if (!storedUserInfo.id().equals(id)) throw new IllegalArgumentException("StoredUserInfo have an [id] different to path variable");

		UserInfo currentUser = userService.getCurrentUser();
		if (!currentUser.isAdmin())
			throw new UserIsNotAllowedException("Current user is not allowed to update a stored user.");

		Optional<StoredUserInfo> stored = storedUserInfoRepository.findById(id);
		if (stored.isEmpty())
			return Optional.empty();

		return Optional.of(storedUserInfoRepository.save(storedUserInfo));
	}

	public void deleteStoredUser(@NonNull String id)
			throws UserIsNotAllowedException
	{
		UserInfo currentUser = userService.getCurrentUser();
		if (!currentUser.isAdmin())
			throw new UserIsNotAllowedException("Current user is not allowed to delete a stored user.");

		Optional<StoredUserInfo> stored = storedUserInfoRepository.findById(id);
		if (stored.isEmpty())
			throw new NoSuchElementException("Can't delete, StoredUserInfo with ID \"%s\" found.".formatted(id));

		storedUserInfoRepository.deleteById(id);
	}

// ####################################################################################
//               Called by and allowed for authorized users
// ####################################################################################

	public String getDenialReasonForCurrentUser()
			throws UserIsNotAllowedException
	{
		UserInfo currentUser = userService.getCurrentUser();
		if (!currentUser.isAuthenticated())
			throw new UserIsNotAllowedException("Current user is not allowed to do this operation.");

		Optional<StoredUserInfo> storedUserInfo = storedUserInfoRepository.findById(currentUser.userDbId());

		return storedUserInfo
				.map(StoredUserInfo::denialReason) // message or null (-> no message -> not denied, "please wait")
				.orElse(null); // no message -> not denied, "please wait"
	}
}

