package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.TestRunService;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserIsNotAllowedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestRunController {

	private final TestRunService testRunService;

	@GetMapping("/api/scenario/{scenarioId}/testrun")
	public List<TestRun> getTestRunsOfScenario(@PathVariable String scenarioId) throws UserIsNotAllowedException {
		return testRunService.getTestRunsOfScenario(scenarioId);
	}

	@PostMapping("/api/scenario/{scenarioId}/testrun")
	public TestRun addTestRun(@PathVariable String scenarioId, @RequestBody TestRun testRun) throws UserIsNotAllowedException {
		return testRunService.addTestRun(scenarioId, testRun);
	}

}
