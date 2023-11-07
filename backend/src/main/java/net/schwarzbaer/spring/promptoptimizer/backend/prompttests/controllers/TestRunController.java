package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewTestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.RunningTestRunsList;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.TestRunService;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestRunController {

	private final TestRunService testRunService;
	private final RunningTestRunsList runningTestRunsList;

	@GetMapping("/api/scenario/{scenarioId}/testrun")
	public List<TestRun> getTestRunsOfScenario(@PathVariable String scenarioId) throws UserIsNotAllowedException {
		return testRunService.getTestRunsOfScenario(scenarioId);
	}

	@PostMapping("/api/scenario/{scenarioId}/testrun")
	public TestRun addTestRun(@PathVariable String scenarioId, @RequestBody TestRun testRun) throws UserIsNotAllowedException {
		return testRunService.addTestRun(scenarioId, testRun);
	}

	@PostMapping("/api/testrun")
	public void performTestRun(@RequestBody NewTestRun newTestRun) throws UserIsNotAllowedException {
		testRunService.performTestRun(newTestRun);
	}

	@GetMapping("/api/scenario/{scenarioId}/testrunstate")
	public List<RunningTestRunsList.ListEntryDTO> getRunningTestRunsOfScenario(@PathVariable String scenarioId) {
		return runningTestRunsList.getEntries(scenarioId);
	}

}
