package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.ScenarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenario")
@RequiredArgsConstructor
public class ScenarioController {

	private final ScenarioService scenarioService;

	@GetMapping
	public List<Scenario> getAllScenariosOfUser() {
		return scenarioService.getAllScenariosOfUser();
	}

	@GetMapping("all")
	public List<Scenario> getAllScenarios() {
		return scenarioService.getAllScenarios();
	}

	@PostMapping
	public Scenario addScenarios(@RequestBody NewScenario newScenario) {
		return scenarioService.addScenarios(newScenario);
	}

}
