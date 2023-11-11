package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewTestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.RunningTestRunsList;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.TestRunService;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestRunController {

	private final TestRunService testRunService;
	private final RunningTestRunsList runningTestRunsList;

	@GetMapping("/api/scenario/{scenarioId}/testrun") // allowed: admin & user
	public List<TestRun> getTestRunsOfScenario(@NonNull @PathVariable String scenarioId) throws UserIsNotAllowedException {
		return testRunService.getTestRunsOfScenario(scenarioId);
	}

	@PostMapping("/api/scenario/{scenarioId}/testrun") // allowed: admin & user
	public TestRun addTestRun(@NonNull @PathVariable String scenarioId, @NonNull @RequestBody TestRun testRun) throws UserIsNotAllowedException {
		return testRunService.addTestRun(scenarioId, testRun);
	}

	@PostMapping("/api/testrun") // allowed: admin & user
	public void performTestRun(@NonNull @RequestBody NewTestRun newTestRun) throws UserIsNotAllowedException {
		testRunService.performTestRun(newTestRun);
	}

	@GetMapping("/api/scenario/{scenarioId}/testrunstate") // allowed: admin & user
	public List<RunningTestRunsList.ListEntryDTO> getRunningTestRunsOfScenario(@NonNull @PathVariable String scenarioId) {
		return runningTestRunsList.getEntries(scenarioId);
	}

}
