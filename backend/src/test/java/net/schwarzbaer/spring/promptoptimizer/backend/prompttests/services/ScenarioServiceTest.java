package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScenarioServiceTest {

	@Mock private ScenarioRepository scenarioRepository;
	@Mock private TestRunRepository testRunRepository;
	@Mock private UserService userService;
	@InjectMocks private ScenarioService scenarioService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

// ####################################################################################
//               getAllScenarios
// ####################################################################################

	@Test
	void whenGetAllScenarios_isCalled_returnsListOfScenarios() {
		// Given
		when(scenarioRepository.findAll()).thenReturn(List.of(
				new Scenario("id1", "author1", "label1", 1),
				new Scenario("id2", "author2", "label2", 1),
				new Scenario("id3", "author2", "label3", 1),
				new Scenario("id4", "author2", "label4", 1)
		));

		// When
		List<Scenario> actual = scenarioService.getAllScenarios();

		// Then
		verify(scenarioRepository).findAll();
		List<Scenario> expected = List.of(
				new Scenario("id1", "author1", "label1", 1),
				new Scenario("id2", "author2", "label2", 1),
				new Scenario("id3", "author2", "label3", 1),
				new Scenario("id4", "author2", "label4", 1)
		);
		assertEquals(expected, actual);
	}

// ####################################################################################
//               getAllScenariosOfUser
// ####################################################################################

	@Test
	void whenGetAllScenariosOfUser_isCalledWithUserWithoutUserDbId_returnsEmptyList() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, true, false,
				"id1", null, "login1", null, null, null, null
		));

		// When
		List<Scenario> actual = scenarioService.getAllScenariosOfUser();

		// Then
		verify(userService).getCurrentUser();
		List<Scenario> expected = List.of();
		assertEquals(expected, actual);
	}

	@Test
	void whenGetAllScenariosOfUser_isCalledWithUserWithUserDbId_returnsList() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, true, false,
				"id1", "author2", "login1", null, null, null, null
		));
		when(scenarioRepository.findByAuthorID("author2")).thenReturn(List.of(
				new Scenario("id2", "author2", "label2", 1),
				new Scenario("id3", "author2", "label3", 1),
				new Scenario("id4", "author2", "label4", 1)
		));

		// When
		List<Scenario> actual = scenarioService.getAllScenariosOfUser();

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).findByAuthorID("author2");
		List<Scenario> expected = List.of(
				new Scenario("id2", "author2", "label2", 1),
				new Scenario("id3", "author2", "label3", 1),
				new Scenario("id4", "author2", "label4", 1)
		);
		assertEquals(expected, actual);
	}

// ####################################################################################
//               addScenarios
// ####################################################################################

	@Test
	void whenAddScenarios_isCalledWithUserWithoutUserDbId_returnsEmptyOptional() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, true, false,
				"id1", null, "login1", null, null, null, null
		));

		// When
		Optional<Scenario> actual = scenarioService.addScenarios(new NewScenario("label"));

		// Then
		verify(userService).getCurrentUser();
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test
	void whenAddScenarios_isCalledWithUserWithUserDbId_returnsEmptyOptional() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, true, false,
				"id1", "author1", "login1", null, null, null, null
		));
		when(scenarioRepository.save(
				new Scenario(null, "author1", "label", null)
		)).thenReturn(
				new Scenario("id2", "author1", "label", null)
		);

		// When
		Optional<Scenario> actual = scenarioService.addScenarios(new NewScenario("label"));

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).save(new Scenario(null, "author1", "label", null));
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id2", "author1", "label", null);
		assertEquals(expected, actual.get());
	}

// ####################################################################################
//               updateScenario
// ####################################################################################

	@Test
	void whenUpdateScenario_isCalledByUser_returnsUpdatedValue() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, true, false,
				"userId1", "author1", null, null, null, null, null
		));
		when(scenarioRepository.findById("id1")).thenReturn(Optional.of(
				new Scenario("id1", "author1", "labelOld", 1)
		));
		when(scenarioRepository.save(
				new Scenario("id1", "author1", "labelNew", 1)
		)).thenReturn(
				new Scenario("id1", "author1", "labelNew", 1)
		);

		// When
		Optional<Scenario> actual = scenarioService.updateScenario(
				"id1", new Scenario("id1", "author1", "labelNew", 1)
		);

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).findById("id1");
		verify(scenarioRepository).save(new Scenario("id1", "author1", "labelNew", 1));
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id1", "author1", "labelNew", 1);
		assertEquals(expected, actual.get());
	}

	@Test void whenUpdateScenario_getsIdDifferentToScenarioID_throwsException() {
		whenUpdateScenario_getsWrongArguments_throwsException("id2", "id1", "author1");
	}
	@Test void whenUpdateScenario_getsScenarioWithNoId_throwsException() {
		whenUpdateScenario_getsWrongArguments_throwsException("id1", null, "author1");
	}
	@Test void whenUpdateScenario_getsScenarioWithNoAuthorId_throwsException() {
		whenUpdateScenario_getsWrongArguments_throwsException("id1", "id1", null);
	}

	private void whenUpdateScenario_getsWrongArguments_throwsException(
			@NonNull String pathId, String scenId, String scenAuthor
	) {
		// Given
		// When
		Executable call = () -> scenarioService.updateScenario(
				pathId, new Scenario(scenId, scenAuthor, "labelNew", 1)
		);
		// Then
		assertThrows(IllegalArgumentException.class, call);
	}

	@Test
	void whenUpdateScenario_getsScenarioWithUnknownId_returnsEmptyOptional() throws UserIsNotAllowedException {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn(Optional.empty());

		// When
		Optional<Scenario> actual = scenarioService.updateScenario(
				"id1", new Scenario("id1", "author1", "labelNew", 1)
		);

		// Then
		verify(scenarioRepository).findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test
	void whenUpdateScenario_isCalledByAdmin_returnsUpdatedValue() throws UserIsNotAllowedException {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, false, true,
				"userId1", "authorAdmin", null, null, null, null, null
		));
		when(scenarioRepository.findById("id1")).thenReturn(Optional.of(
				new Scenario("id1", "author2", "labelOld", 1)
		));
		when(scenarioRepository.save(
				new Scenario("id1", "author1", "labelNew", 1)
		)).thenReturn(
				new Scenario("id1", "author1", "labelNew", 1)
		);

		// When
		Optional<Scenario> actual = scenarioService.updateScenario(
				"id1", new Scenario("id1", "author1", "labelNew", 1)
		);

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).findById("id1");
		verify(scenarioRepository).save(new Scenario("id1", "author1", "labelNew", 1));
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id1", "author1", "labelNew", 1);
		assertEquals(expected, actual.get());
	}

	@Test void whenUpdateScenario_isCalledByNonAdmin_withNoDbId_throwsException() {
		whenUpdateScenario_isCalledByNonAdmin_withWrongIds_throwsException(     null, "author1", "author1");
	}
	@Test void whenUpdateScenario_isCalledByNonAdmin_withDbIdDifferentToGivenScenario_throwsException() {
		whenUpdateScenario_isCalledByNonAdmin_withWrongIds_throwsException("author1", "author1", "author2");
	}
	@Test void whenUpdateScenario_isCalledByNonAdmin_withDbIdDifferentToStoredScenario_throwsException() {
		whenUpdateScenario_isCalledByNonAdmin_withWrongIds_throwsException("author1", "author2", "author1");
	}

	private void whenUpdateScenario_isCalledByNonAdmin_withWrongIds_throwsException(
			String userDbId, String authorOfStored, String authorOfGiven
	) {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, true, false,
				"userId1", userDbId, null, null, null, null, null
		));
		when(scenarioRepository.findById("id1")).thenReturn(Optional.of(
				new Scenario("id1", authorOfStored, "labelOld", 1)
		));

		// When
		Executable call = () -> scenarioService.updateScenario(
				"id1", new Scenario("id1", authorOfGiven, "labelNew", 1)
		);

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
	}

// ####################################################################################
//               deleteScenario
// ####################################################################################

	@Test void whenDeleteScenario_isCalledByAdmin() throws UserIsNotAllowedException {
		whenDeleteScenario_isCalledByAllowedUser(
				"author1", false, true, "authorAdmin"
		);
	}
	@Test void whenDeleteScenario_isCalledByUser() throws UserIsNotAllowedException {
		whenDeleteScenario_isCalledByAllowedUser(
				"authorA", true, false, "authorA"
		);
	}

	private void whenDeleteScenario_isCalledByAllowedUser(
			String storedAuthorID, boolean isUser, boolean isAdmin, String userDbId
	) throws UserIsNotAllowedException {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn(Optional.of(
				new Scenario("id1", storedAuthorID, "label1", 1)
		));
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, isUser, isAdmin,
				"userId1", userDbId, null, null, null, null, null
		));

		// When
		scenarioService.deleteScenario("id1");

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).findById("id1");
		verify(scenarioRepository).deleteById("id1");
		verify(testRunRepository).deleteAllByScenarioId("id1");
		assertTrue(true); // no exception was thrown
	}

	@Test
	void whenDeleteScenario_isCalledWithUnknownId_throwsException() {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn( Optional.empty() );

		// When
		Executable call = () -> scenarioService.deleteScenario("id1");

		// Then
		assertThrows(NoSuchElementException.class, call);
		verify(scenarioRepository).findById("id1");
	}

	@Test
	void whenDeleteScenario_isCalledByUnauthorized_throwsException() {
		whenDeleteScenario_isCalled_andExceptionIsThrown(
				"Author1", "Author1", "anonymousUser",
				false, false
		);
	}
	@Test
	void whenDeleteScenario_isCalledByUnknownAccount_throwsException() {
		whenDeleteScenario_isCalled_andExceptionIsThrown(
				"Author1", "Author1", "User1",
				true, false
		);
	}
	@Test
	void whenDeleteScenario_isCalledByUserWithNoUserDbId_throwsException() {
		whenDeleteScenario_isCalled_andExceptionIsThrown(
				"Author1", null, "User1",
				true, true
		);
	}
	@Test
	void whenDeleteScenario_isCalledByUserWithOtherUserDbId_throwsException() {
		whenDeleteScenario_isCalled_andExceptionIsThrown(
				"Author2", "Author1", "User1",
				true, true
		);
	}

	private void whenDeleteScenario_isCalled_andExceptionIsThrown(
			String storedAuthorID, String userDbId, String userId,
			boolean isAuthenticated, boolean isUser
	) {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn( Optional.of(
				new Scenario("id1", storedAuthorID, "label1", 1)
		));
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				isAuthenticated, isUser, false,
				userId, userDbId, null, null, null, null, null
		));

		// When
		Executable call = () -> scenarioService.deleteScenario("id1");

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(scenarioRepository).findById("id1");
		verify(userService).getCurrentUser();
	}

// ####################################################################################
//               getScenarioById
// ####################################################################################


	@Test void whenGetScenarioById_isCalledByAdmin() throws UserIsNotAllowedException {
		whenGetScenarioById_isCalledByAllowedUser(
				"author1", false, true, "authorAdmin"
		);
	}
	@Test void whenGetScenarioById_isCalledByUser() throws UserIsNotAllowedException {
		whenGetScenarioById_isCalledByAllowedUser(
				"authorA", true, false, "authorA"
		);
	}

	private void whenGetScenarioById_isCalledByAllowedUser(
			String storedAuthorID, boolean isUser, boolean isAdmin, String userDbId
	) throws UserIsNotAllowedException {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn(Optional.of(
				new Scenario("id1", storedAuthorID, "label1", 1)
		));
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				true, isUser, isAdmin,
				"userId1", userDbId, null, null, null, null, null
		));

		// When
		Optional<Scenario> actual = scenarioService.getScenarioById("id1");

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id1", storedAuthorID, "label1", 1);
		assertEquals(expected, actual.get());
	}

	@Test
	void whenGetScenarioById_isCalledWithUnknownId_returnsEmptyOptional() throws UserIsNotAllowedException {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn( Optional.empty() );

		// When
		Optional<Scenario> actual = scenarioService.getScenarioById("id1");

		// Then
		verify(scenarioRepository).findById("id1");
		assertNotNull(actual);
		assertTrue(actual.isEmpty());
	}

	@Test
	void whenGetScenarioById_isCalledByUnauthorized_andExceptionIsThrown() {
		whenGetScenarioById_isCalled_andExceptionIsThrown(
				"Author1", null, "anonymousUser",
				false, false
		);
	}
	@Test
	void whenGetScenarioById_isCalledByUnknownAccount_andExceptionIsThrown() {
		whenGetScenarioById_isCalled_andExceptionIsThrown(
				"Author1", "Author1", "User1",
				true, false
		);
	}
	@Test
	void whenGetScenarioById_isCalledByUserWithNoUserDbId_andExceptionIsThrown() {
		whenGetScenarioById_isCalled_andExceptionIsThrown(
				"Author1", null, "User1",
				true, true
		);
	}
	@Test
	void whenGetScenarioById_isCalledByUserWithOtherUserDbId_andExceptionIsThrown() {
		whenGetScenarioById_isCalled_andExceptionIsThrown(
				"Author2", "Author1", "User1",
				true, true
		);
	}

	private void whenGetScenarioById_isCalled_andExceptionIsThrown(
			String storedAuthorID, String userDbId, String userId,
			boolean isAuthenticated, boolean isUser
	) {
		// Given
		when(scenarioRepository.findById("id1")).thenReturn( Optional.of(
				new Scenario("id1", storedAuthorID, "label1", 1)
		));
		when(userService.getCurrentUser()).thenReturn(new UserInfo(
				isAuthenticated, isUser, false,
				userId, userDbId, null, null, null, null, null
		));

		// When
		Executable call = () -> scenarioService.getScenarioById("id1");

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(scenarioRepository).findById("id1");
		verify(userService).getCurrentUser();
	}

}
