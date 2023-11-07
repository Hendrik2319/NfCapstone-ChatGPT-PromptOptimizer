package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.Role;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.repositories.StoredUserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
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
						null, null, null, null
				))
		);

		// When
		Optional<StoredUserInfo> actual = storedUserInfoService.getUserById("RegistrationIduserID");

		// Then
		verify(storedUserInfoRepository).findById("RegistrationIduserID");
		StoredUserInfo expected = new StoredUserInfo(
				"RegistrationIduserID", Role.USER, "RegistrationId",
				"userID", "login",
				null, null, null, null
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
				"name1", "location1", "url1", "avatarUrl1"
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
				"name2", "location2", "url2", "avatarUrl2"
		);
		storedUserInfoService.updateUserIfNeeded(storedUserInfo, attrs);

		// Then
		verify(storedUserInfoRepository).save(new StoredUserInfo(
				"RegistrationIDuserID1", Role.UNKNOWN_ACCOUNT, "RegistrationID",
				"userID1", "login1",
				"name1", "location1", "url1", "avatarUrl1"
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
				"name1", "location1", "url1", "avatarUrl1"
		);
		storedUserInfoService.updateUserIfNeeded(storedUserInfo, attrs);

		// Then
		verify(storedUserInfoRepository, times(0)).save(any());
	}

}