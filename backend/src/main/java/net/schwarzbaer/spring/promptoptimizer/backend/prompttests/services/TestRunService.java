package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserIsNotAllowedException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestRunService {

	private final TestRunRepository testRunRepository;
	private final ScenarioService scenarioService;

	public List<TestRun> getTestRunsOfScenario(@NonNull String scenarioId)
			throws UserIsNotAllowedException
	{
		Optional<Scenario> storedScenarioOpt = scenarioService.getScenarioById(scenarioId);
		if (storedScenarioOpt.isEmpty()) return List.of();
		// if it's not empty user is allowed to have scenario access

		return testRunRepository.findAllByScenarioId(scenarioId);
	}

	public TestRun addTestRun(@NonNull String scenarioId, @NonNull TestRun testRun) throws UserIsNotAllowedException {
		if (testRun.id()!=null)
			throw new IllegalArgumentException("A new TestRun must not have an ID if it is to be added to the database.");
		if (!scenarioId.equals(testRun.scenarioId()))
			throw new IllegalArgumentException(
					"Given scenarioId (\""+scenarioId+"\") is different to"+
					" scenarioId (\""+testRun.scenarioId()+"\") in given TestRun"
			);

		Optional<Scenario> storedScenarioOpt = scenarioService.getScenarioById(scenarioId);
		if (storedScenarioOpt.isEmpty())
			throw new NoSuchElementException("Can't add TestRun, no Scenario with ID \"%s\" found.".formatted(scenarioId));

		return testRunRepository.save(testRun);
	}
}
