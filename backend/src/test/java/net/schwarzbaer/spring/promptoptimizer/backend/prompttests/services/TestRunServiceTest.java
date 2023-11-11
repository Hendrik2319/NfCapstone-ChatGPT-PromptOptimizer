package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.Answer;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.services.ChatGptService;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.Prompt;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewTestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.lang.NonNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestRunServiceTest {

	@Mock private TestRunRepository testRunRepository;
	@Mock private ScenarioService scenarioService;
	@Mock private ChatGptService chatGptService;
	@Mock private TimeService timeService;
	@Mock private RunningTestRunsList runningTestRunsList;
	@Mock private RunningTestRunsList.ListEntry runningTestRunsListEntry;
	@InjectMocks private TestRunService testRunService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@NonNull
	private static TestRun createTestRun(String testRunId, String scenarioId) {
		return new TestRun(
				testRunId, scenarioId,
				ZonedDateTime.of(2023, 10, 29, 14, 30, 0, 0, ZoneId.systemDefault()),
				"prompt", List.of("var1", "var2"),
				List.of(Map.of("var1", List.of("value1"), "var2", List.of("value2"))),
				List.of(new TestRun.TestAnswer(1, "label", "answer", 12, 23, 35)),
				35.0
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
			Optional.of(new Scenario("scenarioId1", "author1", "label1", 1))
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
				Optional.of(new Scenario("scenarioId1", "author1", "label1", 1))
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
				Optional.of(new Scenario("scenarioId1", "author1", "label1", 1))
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

// ####################################################################################
//               performTestRun
// ####################################################################################

	@Test void whenPerformTestRun_isCalledWithoutScenarioId_throwsException() {
		whenPerformTestRun_isCalledWithWrongNewTestRun_throwsException(null, "TestPrompt", List.of(), List.of());
	}
	@Test void whenPerformTestRun_isCalledWithoutPrompt_throwsException() {
		whenPerformTestRun_isCalledWithWrongNewTestRun_throwsException("scenarioId", null, List.of(), List.of());
	}
	@Test void whenPerformTestRun_isCalledWithoutVariables_throwsException() {
		whenPerformTestRun_isCalledWithWrongNewTestRun_throwsException("scenarioId", "TestPrompt", null, List.of());
	}
	@Test void whenPerformTestRun_isCalledWithoutTestcases_throwsException() {
		whenPerformTestRun_isCalledWithWrongNewTestRun_throwsException("scenarioId", "TestPrompt", List.of(), null);
	}

	private void whenPerformTestRun_isCalledWithWrongNewTestRun_throwsException(
		String scenarioId, String prompt, List<String> variables, List<Map<String, List<String>>> testcases
	) {
		// Given

		// When
		Executable call = () -> testRunService.performTestRun(new NewTestRun(
				scenarioId, prompt, variables, testcases
		));

		// Then
		assertThrows(IllegalArgumentException.class, call);
	}

	@Test
	void whenPerformTestRun_isCalledByNotAllowedUser_throwsException() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenThrow(UserIsNotAllowedException.class);

		// When
		Executable call = () -> testRunService.performTestRun(new NewTestRun(
				"scenarioId1", "TestPrompt", List.of(), List.of()
		));

		// Then
		assertThrows(UserIsNotAllowedException.class, call);
	}

	@Test
	void whenPerformTestRun_isCalledWithUnknownScenarioID_throwsException() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(
				Optional.empty()
		);

		// When
		Executable call = () -> testRunService.performTestRun(new NewTestRun(
				"scenarioId1", "TestPrompt", List.of(), List.of()
		));

		// Then
		assertThrows(NoSuchElementException.class, call);
	}

	@Test
	void whenPerformTestRun_isCalledNormal_returnsNothing() throws UserIsNotAllowedException {
		// Given
		when(scenarioService.getScenarioById("scenarioId1")).thenReturn(
				Optional.of(new Scenario("scenarioId1", "author1", "label1", 1))
		);
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.1/value2.1"))).thenReturn( new Answer("TestAnswer/value1.1/value2.1", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.1/value2.2"))).thenReturn( new Answer("TestAnswer/value1.1/value2.2", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.2/value2.1"))).thenReturn( new Answer("TestAnswer/value1.2/value2.1", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.2/value2.2"))).thenReturn( new Answer("TestAnswer/value1.2/value2.2", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.3/value2.1"))).thenThrow(RuntimeException.class);
//		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.3/value2.1"))).thenReturn( new Answer("TestAnswer/value1.3/value2.1", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.3/value2.2"))).thenReturn( new Answer("TestAnswer/value1.3/value2.2", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.4/value2.3"))).thenReturn( new Answer("TestAnswer/value1.4/value2.3", 12,23,35) );
		when(chatGptService.askChatGPT(new Prompt("TestPrompt/value1.4/value2.4"))).thenReturn( new Answer("TestAnswer/value1.4/value2.4", 12,23,35) );

		ZonedDateTime now = ZonedDateTime.now();
		when(timeService.getNow()).thenReturn(now);

		when(runningTestRunsList.createNewEntry("scenarioId1")).thenReturn(runningTestRunsListEntry);

		// When
		testRunService.performTestRun(new NewTestRun(
				"scenarioId1", "TestPrompt/{var1}/{var2}",
				List.of("var1","var2"),
				List.of(
						Map.of(
								"var1", List.of("value1.1", "value1.2", "value1.3"),
								"var2", List.of("value2.1", "value2.2")
						),
						Map.of(
								"var1", List.of("value1.4"),
								"var2", List.of("value2.3", "value2.4")
						)
				)
		));

		// Then
		verify(scenarioService).getScenarioById("scenarioId1");
		verify(timeService).getNow();

		verify(runningTestRunsList).createNewEntry("scenarioId1");
		verify(runningTestRunsListEntry).setValues(0,8, "TestPrompt/value1.1/value2.1","TestCase 1 { var1:\"value1.1\" var2:\"value2.1\" }");
		verify(runningTestRunsListEntry).setValues(1,8, "TestPrompt/value1.1/value2.2","TestCase 1 { var1:\"value1.1\" var2:\"value2.2\" }");
		verify(runningTestRunsListEntry).setValues(2,8, "TestPrompt/value1.2/value2.1","TestCase 1 { var1:\"value1.2\" var2:\"value2.1\" }");
		verify(runningTestRunsListEntry).setValues(3,8, "TestPrompt/value1.2/value2.2","TestCase 1 { var1:\"value1.2\" var2:\"value2.2\" }");
//		verify(runningTestRunsListEntry).setValues(4,8, "TestPrompt/value1.3/value2.1","TestCase 1 { var1:\"value1.3\" var2:\"value2.1\" }");
		verify(runningTestRunsListEntry).setValues(4,8, "TestPrompt/value1.3/value2.2","TestCase 1 { var1:\"value1.3\" var2:\"value2.2\" }");
		verify(runningTestRunsListEntry).setValues(5,8, "TestPrompt/value1.4/value2.3","TestCase 2 { var1:\"value1.4\" var2:\"value2.3\" }");
		verify(runningTestRunsListEntry).setValues(6,8, "TestPrompt/value1.4/value2.4","TestCase 2 { var1:\"value1.4\" var2:\"value2.4\" }");
		verify(runningTestRunsList).removeEntry("scenarioId1", Objects.requireNonNull( runningTestRunsListEntry ) );

		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.1/value2.1"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.1/value2.2"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.2/value2.1"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.2/value2.2"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.3/value2.1"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.3/value2.2"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.4/value2.3"));
		verify(chatGptService).askChatGPT(new Prompt("TestPrompt/value1.4/value2.4"));

		verify(testRunRepository).save(new TestRun(
				null, "scenarioId1", now,
				"TestPrompt/{var1}/{var2}",
				List.of("var1","var2"),
				List.of(
						Map.of(
								"var1", List.of("value1.1", "value1.2", "value1.3"),
								"var2", List.of("value2.1", "value2.2")
						),
						Map.of(
								"var1", List.of("value1.4"),
								"var2", List.of("value2.3", "value2.4")
						)
				),
				List.of(
						new TestRun.TestAnswer(0, "TestCase 1 { var1:\"value1.1\" var2:\"value2.1\" }","TestAnswer/value1.1/value2.1",12,23,35),
						new TestRun.TestAnswer(0, "TestCase 1 { var1:\"value1.1\" var2:\"value2.2\" }","TestAnswer/value1.1/value2.2",12,23,35),
						new TestRun.TestAnswer(0, "TestCase 1 { var1:\"value1.2\" var2:\"value2.1\" }","TestAnswer/value1.2/value2.1",12,23,35),
						new TestRun.TestAnswer(0, "TestCase 1 { var1:\"value1.2\" var2:\"value2.2\" }","TestAnswer/value1.2/value2.2",12,23,35),
//						new TestRun.TestAnswer(0, "TestCase 1 { var1:\"value1.3\" var2:\"value2.1\" }","TestAnswer/value1.3/value2.1",12,23,35),
						new TestRun.TestAnswer(0, "TestCase 1 { var1:\"value1.3\" var2:\"value2.2\" }","TestAnswer/value1.3/value2.2",12,23,35),
						new TestRun.TestAnswer(1, "TestCase 2 { var1:\"value1.4\" var2:\"value2.3\" }","TestAnswer/value1.4/value2.3",12,23,35),
						new TestRun.TestAnswer(1, "TestCase 2 { var1:\"value1.4\" var2:\"value2.4\" }","TestAnswer/value1.4/value2.4",12,23,35)
				),
				35.d
		));
	}


	@Test
	void whenComputeAverageTokensPerRequest_isCalledWithEmptyList_returnsNull() {
		// Given

		// When
		Double actual = testRunService.computeAverageTokensPerRequest( Objects.requireNonNull( List.of() ) );

		// Then
		assertNull(actual);
	}

	@Test
	void whenComputeAverageTokensPerRequest_isCalledWithList_returnsACorrectResult() {
		// Given

		// When
		Double actual = testRunService.computeAverageTokensPerRequest( Objects.requireNonNull( List.of(
				new TestRun.TestAnswer(0, "label1","answer1",null,null,null), // --
				new TestRun.TestAnswer(0, "label1","answer1",null,   1,null), // 1
				new TestRun.TestAnswer(0, "label1","answer1",   2,null,null), // 2
				new TestRun.TestAnswer(0, "label1","answer1",   3,   4,null), // 7
				new TestRun.TestAnswer(0, "label1","answer1",null,null,   5), // 5
				new TestRun.TestAnswer(0, "label1","answer1",null,   6,   6), // 6
				new TestRun.TestAnswer(0, "label1","answer1",   7,null,   7), // 7
				new TestRun.TestAnswer(0, "label1","answer1",   8,   9,  17)  // 17
		)));

		// Then
		double expected = (1 + 2 + 7 + 5 + 6 + 7 + 17) / 7.0;
		assertNotNull(actual);
		assertTrue(Math.abs((actual/expected)-1) < 0.000001);
		// actual is inside of a range of +/-1ppm around expected
	}
}
