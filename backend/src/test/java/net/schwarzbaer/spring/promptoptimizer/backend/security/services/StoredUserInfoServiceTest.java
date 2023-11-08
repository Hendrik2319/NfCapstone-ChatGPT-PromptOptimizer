package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.StoredUserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoredUserInfoServiceTest {

	@Mock private StoredUserInfoRepository storedUserInfoRepository;
	@Mock private UserService userService;
	@InjectMocks private StoredUserInfoService storedUserInfoService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

// ####################################################################################
//               getUserById
// ####################################################################################

	@Test
	void whenGetUserById_getsUnknownId_returnsEmptyOptional() {
		// Given
		when(storedUserInfoRepository.findById(any())).thenReturn(
				Optional.empty()
		);

		// When
		Optional<StoredUserInfo> actual = storedUserInfoService.getUserById("userDbId");

		// Then
		verify(storedUserInfoRepository).findById("userDbId");
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test
	void whenGetUserById_getsKnownId_returnsOptionalWithData() {
		// Given
		when(storedUserInfoRepository.findById(any())).thenReturn(
				Optional.of(new StoredUserInfo(
						"RegistrationIduserID", Role.USER, "RegistrationId",
						"userID", "login",
						null, null, null, null, null
				))
		);

		// When
		Optional<StoredUserInfo> actual = storedUserInfoService.getUserById("RegistrationIduserID");

		// Then
		verify(storedUserInfoRepository).findById("RegistrationIduserID");
		StoredUserInfo expected = new StoredUserInfo(
				"RegistrationIduserID", Role.USER, "RegistrationId",
				"userID", "login",
				null, null, null, null, null
		);
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		assertEquals(expected, actual.get());
	}

// ####################################################################################
//               addUser
// ####################################################################################

	@Test
	void whenAddUser_isCalled() {
		// Given
		// When
		Map<String, Object> attrs = Map.of(
				"UserDbId"  , "RegistrationIDuserID1",
				"id"        , "userID1"  ,
				"login"     , "login1"   ,
				"name"      , "name1"    ,
				"location"  , "location1",
				"html_url"  , "url1"     ,
				"avatar_url", "avatarUrl1"
		);
		storedUserInfoService.addUser(Role.UNKNOWN_ACCOUNT, "RegistrationID", attrs);

		// Then
		verify(storedUserInfoRepository).save(new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1", null
		));
	}

// ####################################################################################
//               updateUserIfNeeded
// ####################################################################################

	@Test
	void whenUpdateUserIfNeeded_isCalledWithNewData() {
		// Given
		// When
		Map<String, Object> attrs = Map.of(
				"UserDbId"  , "RegistrationIDuserID1",
				"id"        , "userID1"  ,
				"login"     , "login1"   ,
				"name"      , "name1"    ,
				"location"  , "location1",
				"html_url"  , "url1"     ,
				"avatar_url", "avatarUrl1"
		);
		StoredUserInfo storedUserInfo = new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login2",
				"name2", "location2", "url2", "avatarUrl2", "reason1"
		);
		storedUserInfoService.updateUserIfNeeded(storedUserInfo, attrs);

		// Then
		verify(storedUserInfoRepository).save(new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1", "reason1"
		));
	}

	@Test void whenUpdateUserIfNeeded_isCalledWithNoData() {
		whenUpdateUserIfNeeded_isCalled_andNothingIsWrittenToRepo(Map.of());
	}
	@Test void whenUpdateUserIfNeeded_isCalledWithSameData() {
		whenUpdateUserIfNeeded_isCalled_andNothingIsWrittenToRepo(Map.of(
				"UserDbId"  , "RegistrationIDuserID1",
				"id"        , "userID1"  ,
				"login"     , "login1"   ,
				"name"      , "name1"    ,
				"location"  , "location1",
				"html_url"  , "url1"     ,
				"avatar_url", "avatarUrl1"
		));
	}
	private void whenUpdateUserIfNeeded_isCalled_andNothingIsWrittenToRepo(Map<String, Object> attrs) {
		// Given

		// When
		StoredUserInfo storedUserInfo = new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1", "reason1"
		);
		storedUserInfoService.updateUserIfNeeded(storedUserInfo, attrs);

		// Then
		verify(storedUserInfoRepository, times(0)).save(any());
	}

// ####################################################################################
//               getAllStoredUsers
// ####################################################################################

	@NonNull
	private static StoredUserInfo createStoredUserInfo(Role role, String registrationId, String originalId, int index) {
		return new StoredUserInfo(
				registrationId + originalId, role, registrationId, originalId,
				"login" + index, "name" + index, "location" + index, "url" + index,
				"avatarUrl" + index, "reason" + index
		);
	}

	@Test
	void whenGetAllStoredUsers_isCalledByAdmin_returnsList() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, true, true,
				"userId1", "RegistrationIduserId1", "login1", null, null, null, null
		));
		when(storedUserInfoRepository.findAll()).thenReturn(List.of(
				createStoredUserInfo(Role.ADMIN          , "RegistrationId", "userId1", 1),
				createStoredUserInfo(Role.USER           , "RegistrationId", "userId2", 2),
				createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "RegistrationId", "userId3", 3),
				createStoredUserInfo(Role.USER           , "RegistrationId", "userId4", 4),
				createStoredUserInfo(Role.USER           , "RegistrationId", "userId5", 5)
		));

		// When
		List<StoredUserInfo> actual = storedUserInfoService.getAllStoredUsers();

		// Then
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findAll();
		List<Object> expected = List.of(
				createStoredUserInfo(Role.ADMIN          , "RegistrationId", "userId1", 1),
				createStoredUserInfo(Role.USER           , "RegistrationId", "userId2", 2),
				createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "RegistrationId", "userId3", 3),
				createStoredUserInfo(Role.USER           , "RegistrationId", "userId4", 4),
				createStoredUserInfo(Role.USER           , "RegistrationId", "userId5", 5)
		);
		assertEquals(expected, actual);
	}

	@Test void whenGetAllStoredUsers_isCalledByUser_throwsException() {
		whenGetAllStoredUsers_isCalledByNotAllowedUser_throwsException(
				true, true, "userId1", "RegistrationIduserId1", "login1");
	}
	@Test void whenGetAllStoredUsers_isCalledByUnknownAccount_throwsException() {
		whenGetAllStoredUsers_isCalledByNotAllowedUser_throwsException(
				true, false, "userId1", "RegistrationIduserId1", "login1");
	}
	@Test void whenGetAllStoredUsers_isCalledByUnauthorized_throwsException() {
		whenGetAllStoredUsers_isCalledByNotAllowedUser_throwsException(
				false, false, "anonymousUser", null, null);
	}
	private void whenGetAllStoredUsers_isCalledByNotAllowedUser_throwsException(
			boolean isAuthenticated, boolean isUser, String id, String userDbId, String login
	) {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				isAuthenticated, isUser, false,
				id, userDbId, login, null, null, null, null
		));

		// When
		Executable call = () -> storedUserInfoService.getAllStoredUsers();

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(userService).getCurrentUser();
	}

// ####################################################################################
//               updateStoredUser
// ####################################################################################

	@Test
	void whenUpdateStoredUser_isCalledNormal_returnsUpdatedData() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, false, true,
				"id", "userDbId", "login", null, null, null, null
		));
		when(storedUserInfoRepository.findById("RegistrationId2userId1")).thenReturn(
			Optional.of(
				createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "RegistrationId2", "userId1", 2)
			)
		);
		when(storedUserInfoRepository.save(
				createStoredUserInfo(Role.USER, "RegistrationId2", "userId1", 1)
		)).thenReturn(
				createStoredUserInfo(Role.USER, "RegistrationId2", "userId1", 1)
		);

		// When
		Optional<StoredUserInfo> actual = storedUserInfoService.updateStoredUser("RegistrationId2userId1",
				createStoredUserInfo(Role.USER, "RegistrationId2", "userId1", 1)
		);

		// Then
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findById("RegistrationId2userId1");
		verify(storedUserInfoRepository).save(
				createStoredUserInfo(Role.USER, "RegistrationId2", "userId1", 1)
		);
		StoredUserInfo expected = createStoredUserInfo(Role.USER, "RegistrationId2", "userId1", 1);
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		assertEquals(expected, actual.get());
	}

	@Test
	void whenUpdateStoredUser_isCalledWithUnknownId_returnsEmptyOptional() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, false, true,
				"id", "userDbId", "login", null, null, null, null
		));
		when(storedUserInfoRepository.findById("RegistrationIduserId1")).thenReturn(
			Optional.empty()
		);

		// When
		Optional<StoredUserInfo> actual = storedUserInfoService.updateStoredUser("RegistrationIduserId1",
				createStoredUserInfo(Role.USER, "RegistrationId", "userId1", 1)
		);

		// Then
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findById("RegistrationIduserId1");
		verify(storedUserInfoRepository, times(0)).save(any());
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test void whenUpdateStoredUser_isCalledByUser_throwsException() {
		whenUpdateStoredUser_isCalledByNonAdmin_throwsException(
				true, true, "userId1", "RegistrationIduserId1", "login1");
	}
	@Test void whenUpdateStoredUser_isCalledByUnknownAccount_throwsException() {
		whenUpdateStoredUser_isCalledByNonAdmin_throwsException(
				true, false, "userId1", "RegistrationIduserId1", "login1");
	}
	@Test void whenUpdateStoredUser_isCalledByUnauthorized_throwsException() {
		whenUpdateStoredUser_isCalledByNonAdmin_throwsException(
				false, false, "anonymousUser", null, null);
	}
	private void whenUpdateStoredUser_isCalledByNonAdmin_throwsException(
			boolean isAuthenticated, boolean isUser, String id, String userDbId, String login
	) {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				isAuthenticated, isUser, false,
				id, userDbId, login, null, null, null, null
		));

		// When
		Executable call = () -> storedUserInfoService.updateStoredUser("RegistrationIduserId1",
				createStoredUserInfo(Role.USER, "RegistrationId", "userId1", 1)
		);

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository, times(0)).findById(any());
		verify(storedUserInfoRepository, times(0)).save(any());
	}

	@Test void whenUpdateStoredUser_isCalledWithNoIdInStoredData_throwsException() {
		whenUpdateStoredUser_isCalledWithWrongIDs_throwsException(
				"RegistrationIduserId1",
				null
		);
	}
	@Test void whenUpdateStoredUser_isCalledWithDifferentIds_throwsException() {
		whenUpdateStoredUser_isCalledWithWrongIDs_throwsException(
				"RegistrationIduserId2",
				"RegistrationIduserId1"
		);
	}
	private void whenUpdateStoredUser_isCalledWithWrongIDs_throwsException(String idInPath, String idInUserInfo) {
		// Given

		// When
		Executable call = () -> storedUserInfoService.updateStoredUser(idInPath,
				new StoredUserInfo(
						idInUserInfo, Role.USER, "RegistrationId", "userId1",
						"login" + 1, "name" + 1, "location" + 1, "url" + 1,
						"avatarUrl" + 1, "reason" + 1
				)
		);

		// Then
		assertThrows(IllegalArgumentException.class, call);
		verify(userService, times(0)).getCurrentUser();
		verify(storedUserInfoRepository, times(0)).findById(any());
		verify(storedUserInfoRepository, times(0)).save(any());
	}

// ####################################################################################
//               deleteStoredUser
// ####################################################################################

	@Test
	void whenDeleteStoredUser_isCalledNormal() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, false, true,
				"id", "userDbId", "login", null, null, null, null
		));
		when(storedUserInfoRepository.findById("RegistrationIduserId1")).thenReturn(
				Optional.of(
						createStoredUserInfo(Role.UNKNOWN_ACCOUNT, "RegistrationId", "userId1", 2)
				)
		);

		// When
		storedUserInfoService.deleteStoredUser("RegistrationIduserId1");

		// Then
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findById("RegistrationIduserId1");
		verify(storedUserInfoRepository).deleteById("RegistrationIduserId1");
	}

	@Test
	void whenDeleteStoredUser_isCalledWithUnknownId_throwsException() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, false, true,
				"id", "userDbId", "login", null, null, null, null
		));
		when(storedUserInfoRepository.findById("RegistrationIduserId1")).thenReturn(
				Optional.empty()
		);

		// When
		Executable call = () ->
			storedUserInfoService.deleteStoredUser("RegistrationIduserId1");

		// Then
		assertThrows(NoSuchElementException.class, call);
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findById("RegistrationIduserId1");
		verify(storedUserInfoRepository, times(0)).deleteById(any());
	}

	@Test void whenDeleteStoredUser_isCalledByUser_throwsException() {
		whenDeleteStoredUser_isCalledByNonAdmin_throwsException(
				true, true, "userId1", "RegistrationIduserId1", "login1");
	}
	@Test void whenDeleteStoredUser_isCalledByUnknownAccount_throwsException() {
		whenDeleteStoredUser_isCalledByNonAdmin_throwsException(
				true, false, "userId1", "RegistrationIduserId1", "login1");
	}
	@Test void whenDeleteStoredUser_isCalledByUnauthorized_throwsException() {
		whenDeleteStoredUser_isCalledByNonAdmin_throwsException(
				false, false, "anonymousUser", null, null);
	}
	private void whenDeleteStoredUser_isCalledByNonAdmin_throwsException(
			boolean isAuthenticated, boolean isUser, String id, String userDbId, String login
	) {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				isAuthenticated, isUser, false,
				id, userDbId, login, null, null, null, null
		));

		// When
		Executable call = () ->
				storedUserInfoService.deleteStoredUser("RegistrationIduserId1");

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository, times(0)).findById(any());
		verify(storedUserInfoRepository, times(0)).deleteById(any());
	}

// ####################################################################################
//               getDenialReasonForCurrentUser
// ####################################################################################

	@Test
	void whenGetDenialReasonForCurrentUser_isCalledNormal_returnsString()
			throws UserIsNotAllowedException
	{
		whenGetDenialReasonForCurrentUser_isCalledNormal_returnsReason(
				"TestReason", "TestReason");
	}
	@Test
	void whenGetDenialReasonForCurrentUser_isCalledNormalWithnoStoredReason_returnsNull()
			throws UserIsNotAllowedException
	{
		whenGetDenialReasonForCurrentUser_isCalledNormal_returnsReason(
				null, null);
	}
	private void whenGetDenialReasonForCurrentUser_isCalledNormal_returnsReason(
			String storedReason, String expectedReason
	) throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, false, false,
				"id", "RegistrationIduserId1", "login", null, null, null, null
		));
		when(storedUserInfoRepository.findById("RegistrationIduserId1")).thenReturn(
				Optional.of(
						new StoredUserInfo(
								"RegistrationIduserId1",
								null, null, null,
								null, null, null, null,
								null, storedReason
						)
				)
		);

		// When
		String actual = storedUserInfoService.getDenialReasonForCurrentUser();

		// Then
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findById("RegistrationIduserId1");
		assertEquals(expectedReason, actual);
	}

	@Test
	void whenGetDenialReasonForCurrentUser_isCalledWithUnknownUserId_returnsNull() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, false, false,
				"id", "RegistrationIduserId1", "login", null, null, null, null
		));
		when(storedUserInfoRepository.findById("RegistrationIduserId1")).thenReturn(
				Optional.empty()
		);

		// When
		String actual = storedUserInfoService.getDenialReasonForCurrentUser();

		// Then
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository).findById("RegistrationIduserId1");
		assertNull(actual);
	}

	@Test
	void whenGetDenialReasonForCurrentUser_isCalledByUnauthorized_throwsException() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				false, false, false,
				"anonymousUser", null, null, null, null, null, null
		));

		// When
		Executable call = () ->
			storedUserInfoService.getDenialReasonForCurrentUser();

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(userService).getCurrentUser();
		verify(storedUserInfoRepository, times(0)).findById(any());
	}
}