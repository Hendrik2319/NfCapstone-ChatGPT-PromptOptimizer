package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.TestRunService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestRunController {

	private final TestRunService testRunService;

	@GetMapping("/api/scenario/{scenarioId}/testrun")
	public List<TestRun> getAllTestRunOfScenarios(@PathVariable String scenarioId) {
		return testRunService.getAllTestRunOfScenarios(scenarioId);
	}

	@PostMapping("/api/scenario/{scenarioId}/testrun")
	public TestRun addTestRun(@PathVariable String scenarioId, @RequestBody TestRun testRun) {
		return testRunService.addTestRun(scenarioId, testRun);
	}

	@PostMapping("/api/testrunexample")
	public TestRun addExampleTestRun(@RequestBody String prompt) {
		return testRunService.addExampleTestRun(prompt);
	}

}
