package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScenarioServiceTest {

	private ScenarioRepository scenarioRepository;
	private UserService userService;
	private ScenarioService scenarioService;

	@BeforeEach
	void setup() {
		scenarioRepository = mock(ScenarioRepository.class);
		userService = mock(UserService.class);
		scenarioService = new ScenarioService(scenarioRepository, userService);
	}

	@Test
	void whenGetAllScenarios_isCalled_returnsListOfScenarios() {
		// Given
		when(scenarioRepository.findAll()).thenReturn(List.of(
				new Scenario("id1", "author1", "label1"),
				new Scenario("id2", "author2", "label2"),
				new Scenario("id3", "author2", "label3"),
				new Scenario("id4", "author2", "label4")
		));

		// When
		List<Scenario> actual = scenarioService.getAllScenarios();

		// Then
		verify(scenarioRepository).findAll();
		List<Scenario> expected = List.of(
				new Scenario("id1", "author1", "label1"),
				new Scenario("id2", "author2", "label2"),
				new Scenario("id3", "author2", "label3"),
				new Scenario("id4", "author2", "label4")
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenGetAllScenariosOfUser_isCalledWithUserWithoutUserDbId_returnsEmptyList() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
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
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, true, false,
				"id1", "author2", "login1", null, null, null, null
		));
		when(scenarioRepository.findByAuthorID("author2")).thenReturn(List.of(
				new Scenario("id2", "author2", "label2"),
				new Scenario("id3", "author2", "label3"),
				new Scenario("id4", "author2", "label4")
		));

		// When
		List<Scenario> actual = scenarioService.getAllScenariosOfUser();

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).findByAuthorID("author2");
		List<Scenario> expected = List.of(
				new Scenario("id2", "author2", "label2"),
				new Scenario("id3", "author2", "label3"),
				new Scenario("id4", "author2", "label4")
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenAddScenarios_isCalledWithUserWithoutUserDbId_returnsEmptyOptional() {
		// Given
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
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
		when(userService.getCurrentUser()).thenReturn(new UserInfos(
				true, true, false,
				"id1", "author1", "login1", null, null, null, null
		));
		when(scenarioRepository.save(
				new Scenario(null, "author1", "label")
		)).thenReturn(
				new Scenario("id2", "author1", "label")
		);

		// When
		Optional<Scenario> actual = scenarioService.addScenarios(new NewScenario("label"));

		// Then
		verify(userService).getCurrentUser();
		verify(scenarioRepository).save(new Scenario(null, "author1", "label"));
		assertNotNull(actual);
		assertTrue(actual.isPresent());
		Scenario expected = new Scenario("id2", "author1", "label");
		assertEquals(expected, actual.get());
	}
}
