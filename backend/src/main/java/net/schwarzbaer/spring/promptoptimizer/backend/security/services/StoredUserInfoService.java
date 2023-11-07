package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.StoredUserInfoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
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

	public void addUser(Role role, String registrationId, Map<String, Object> newAttributes) {
		storedUserInfoRepository.save(new StoredUserInfo(
				Objects.toString(newAttributes.get(UserService.ATTR_USER_DB_ID ), null),
				role,
				registrationId,
				Objects.toString(newAttributes.get(UserService.ATTR_ORIGINAL_ID), null),
				Objects.toString(newAttributes.get(UserService.ATTR_LOGIN      ), null),
				Objects.toString(newAttributes.get(UserService.ATTR_NAME       ), null),
				Objects.toString(newAttributes.get(UserService.ATTR_LOCATION   ), null),
				Objects.toString(newAttributes.get(UserService.ATTR_URL        ), null),
				Objects.toString(newAttributes.get(UserService.ATTR_AVATAR_URL ), null)
		));
	}

	public void updateUserIfNeeded(StoredUserInfo storedUserInfo, Map<String, Object> newAttributes) {
		StoredUserInfo updatedUserInfo = new StoredUserInfo(
				storedUserInfo.id(),
				storedUserInfo.role(),
				storedUserInfo.registrationId(),
				Objects.toString(newAttributes.get(UserService.ATTR_ORIGINAL_ID), storedUserInfo.originalId()),
				Objects.toString(newAttributes.get(UserService.ATTR_LOGIN      ), storedUserInfo.login     ()),
				Objects.toString(newAttributes.get(UserService.ATTR_NAME       ), storedUserInfo.name      ()),
				Objects.toString(newAttributes.get(UserService.ATTR_LOCATION   ), storedUserInfo.location  ()),
				Objects.toString(newAttributes.get(UserService.ATTR_URL        ), storedUserInfo.url       ()),
				Objects.toString(newAttributes.get(UserService.ATTR_AVATAR_URL ), storedUserInfo.avatar_url())
		);
		if (!updatedUserInfo.equals(storedUserInfo))
			storedUserInfoRepository.save(updatedUserInfo);
	}

// ####################################################################################
//               Called by and allowed for Admin
// ####################################################################################

	public List<StoredUserInfo> getAllStoredUsers()
			throws UserIsNotAllowedException
	{
		UserInfos currentUser = userService.getCurrentUser();
		// if (9)
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

	public String getDenialReasonForCurrentUser() {
		// TODO
		return null;
	}
}

