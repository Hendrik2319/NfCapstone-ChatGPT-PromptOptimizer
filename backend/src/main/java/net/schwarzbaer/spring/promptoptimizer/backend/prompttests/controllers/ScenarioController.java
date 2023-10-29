package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.ScenarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenario")
@RequiredArgsConstructor
public class ScenarioController {

	private final ScenarioService scenarioService;

	@GetMapping
	public List<Scenario> getAllScenariosOfUser()
	{
		return scenarioService.getAllScenariosOfUser();
	}

	@GetMapping("all")
	public List<Scenario> getAllScenarios()
	{
		return scenarioService.getAllScenarios();
	}

	@GetMapping("{id}")
	public ResponseEntity<Scenario> getScenarioById(@PathVariable String id)
			throws ScenarioService.UserIsNotAllowedException
	{
		return ResponseEntity.of(scenarioService.getScenarioById(id));
	}

	@PostMapping
	public ResponseEntity<Scenario> addScenarios(@RequestBody NewScenario newScenario)
	{
		return ResponseEntity.of(scenarioService.addScenarios(newScenario));
	}

	@PutMapping("{id}")
	public ResponseEntity<Scenario> updateScenario(@PathVariable String id, @RequestBody Scenario scenario)
			throws ScenarioService.UserIsNotAllowedException
	{
		return ResponseEntity.of(scenarioService.updateScenario(id, scenario));
	}

	@DeleteMapping("{id}")
	public void deleteScenario(@PathVariable String id)
			throws ScenarioService.UserIsNotAllowedException
	{
		scenarioService.deleteScenario(id);
	}

}
