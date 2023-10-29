package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestRunService {

	private final TestRunRepository testRunRepository;
//	private final UserService userService;

	public List<TestRun> getTestRunsOfScenario(@NonNull String scenarioId) {
		return testRunRepository.findAllByScenarioId(scenarioId);
	}

	public TestRun addTestRun(@NonNull String scenarioId, @NonNull TestRun testRun) {
		if (testRun.id()!=null)
			throw new IllegalArgumentException("A new TestRun must not have an ID if it is to be added to the database.");
		if (!scenarioId.equals(testRun.scenarioId()))
			throw new IllegalArgumentException(
					"Given scenarioId (\""+scenarioId+"\") is different to"+
					" scenarioId (\""+testRun.scenarioId()+"\") in given TestRun"
			);
		return testRunRepository.save(testRun);
	}

	public TestRun addExampleTestRun(@NonNull String prompt) {
		return addTestRun("ScenarioID1", new TestRun(
				null,
				"ScenarioID1",
				ZonedDateTime.now(),
				prompt,
				List.of("Var1", "Var2", "Var3", "Var4", "Var5"),
				List.of(
						Map.of(
								"Var1", List.of("Value1.1"),
								"Var2", List.of("Value1.2"),
								"Var3", List.of("Value1.3"),
								"Var4", List.of("Value1.4"),
								"Var5", List.of("Value1.5")
						),
						Map.of(
								"Var1", List.of("Value2.1"),
								"Var2", List.of("Value2.2"),
								"Var3", List.of("Value2.3"),
								"Var4", List.of("Value2.4"),
								"Var5", List.of("Value2.5")
						)
				),
				List.of(
						new TestRun.TestAnswer(1, "TestCase1", "answer1"),
						new TestRun.TestAnswer(2, "TestCase2", "answer2")
				)
		));
	}
}
