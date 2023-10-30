package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.ChatGptService;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserIsNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.lang.NonNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestRunServiceTest {

	private TestRunRepository testRunRepository;
	private ScenarioService scenarioService;
	private TestRunService testRunService;
	private ChatGptService chatGptService;

	@BeforeEach
	void setup() {
		testRunRepository = mock(TestRunRepository.class);
		scenarioService = mock(ScenarioService.class);
		chatGptService = mock(ChatGptService.class);
		testRunService = new TestRunService(testRunRepository, scenarioService, chatGptService);
	}

	@NonNull
	private static TestRun createTestRun(String testRunId, String scenarioId) {
		return new TestRun(
				testRunId, scenarioId,
				ZonedDateTime.of(2023, 10, 29, 14, 30, 0, 0, ZoneId.systemDefault()),
				"prompt", List.of("var1", "var2"),
				List.of(Map.of("var1", List.of("value1"), "var2", List.of("value2"))),
				List.of(new TestRun.TestAnswer(1, "label", "answer"))
		);
	}

// ####################################################################################
//               getTestRunsOfScenario
// ####################################################################################

	@Test
	void whenGetTestRunsOfScenario_isCalledWithUnknownId_returnsEmptyList() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(Optional.empty());

		// When
		List<TestRun> actual = testRunService.getTestRunsOfScenario("scenarioId1");

		// Then
		verify(scenarioService).getScenarioById("scenarioId1");
		List<Object> expected = List.of();
		assertEquals(expected, actual);
	}

	@Test
	void whenGetTestRunsOfScenario_isCalledByNotAllowedUser_throwsException() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenThrow(UserIsNotAllowedException.class);

		// When
		Executable call = () -> testRunService.getTestRunsOfScenario("scenarioId1");

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(scenarioService).getScenarioById("scenarioId1");
	}

	@Test
	void whenGetTestRunsOfScenario_isCalledByAllowedUser_returnsList1() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(
			Optional.of(new Scenario("scenarioId1", "author1", "label1"))
		);
		when(testRunRepository.findAllByScenarioId("scenarioId1")).thenReturn(
				List.of()
		);

		// When
		List<TestRun> actual = testRunService.getTestRunsOfScenario("scenarioId1");

		// Then
		List<Object> expected = List.of();
		assertEquals(expected, actual);
	}

	@Test
	void whenGetTestRunsOfScenario_isCalledByAllowedUser_returnsList2() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(
				Optional.of(new Scenario("scenarioId1", "author1", "label1"))
		);
		when(testRunRepository.findAllByScenarioId("scenarioId1")).thenReturn(
				List.of(createTestRun("id1", "scenarioId1"))
		);

		// When
		List<TestRun> actual = testRunService.getTestRunsOfScenario("scenarioId1");

		// Then
		List<Object> expected = List.of(createTestRun("id1", "scenarioId1"));
		assertEquals(expected, actual);
	}

// ####################################################################################
//               addTestRun
// ####################################################################################

	@Test
	void whenAddTestRun_isCalledWithTestRunWithID_throwsException() {
		// Given

		// When
		Executable call = () -> testRunService.addTestRun("scenarioId1",
				createTestRun("id1", "scenarioId1")
		);

		// Then
		assertThrows(IllegalArgumentException.class, call);
	}

	@Test
	void whenAddTestRun_isCalledWithTestRunWithIdDifferentToIdFromPath_throwsException() {
		// Given

		// When
		Executable call = () -> testRunService.addTestRun("scenarioId1",
				createTestRun(null, "scenarioId2")
		);

		// Then
		assertThrows(IllegalArgumentException.class, call);
	}

	@Test
	void whenAddTestRun_isCalledWithUnknownId_throwsException() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(
				Optional.empty()
		);

		// When
		Executable call = () -> testRunService.addTestRun("scenarioId1",
				createTestRun(null, "scenarioId1")
		);

		// Then
		assertThrows(NoSuchElementException.class, call);
		verify(scenarioService).getScenarioById("scenarioId1");
	}

	@Test
	void whenAddTestRun_isCalledByNotAllowedUser_throwsException() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenThrow(
				UserIsNotAllowedException.class
		);

		// When
		Executable call = () -> testRunService.addTestRun("scenarioId1",
				createTestRun(null, "scenarioId1")
		);

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
		verify(scenarioService).getScenarioById("scenarioId1");
	}

	@Test
	void whenAddTestRun_isCalledNormal_returnsStoredTestRun() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(
				Optional.of(new Scenario("scenarioId1", "author1", "label1"))
		);
		when(testRunRepository.save(createTestRun(null, "scenarioId1"))).thenReturn(
				createTestRun("id1", "scenarioId1")
		);

		// When
		TestRun actual = testRunService.addTestRun("scenarioId1",
				createTestRun(null, "scenarioId1")
		);

		// Then
		verify(scenarioService).getScenarioById("scenarioId1");
		verify(testRunRepository).save(createTestRun(null, "scenarioId1"));
		TestRun expected = createTestRun("id1", "scenarioId1");
		assertEquals(expected, actual);
	}

}
