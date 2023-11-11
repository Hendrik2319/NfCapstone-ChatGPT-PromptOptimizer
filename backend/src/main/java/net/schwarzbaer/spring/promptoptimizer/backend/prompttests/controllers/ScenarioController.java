package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.ScenarioService;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenario")
@RequiredArgsConstructor
public class ScenarioController {

	private final ScenarioService scenarioService;

	@GetMapping // allowed: admin & user
	public List<Scenario> getAllScenariosOfUser()
	{
		return scenarioService.getAllScenariosOfUser();
	}

	@GetMapping("all") // allowed: admin
	public List<Scenario> getAllScenarios()
	{
		return scenarioService.getAllScenarios();
	}

	@GetMapping("{id}") // allowed: user (author) & admin
	public ResponseEntity<Scenario> getScenarioById(@NonNull @PathVariable String id)
			throws UserIsNotAllowedException
	{
		return ResponseEntity.of(scenarioService.getScenarioById(id));
	}

	@PostMapping // allowed: user & admin   --> author
	public ResponseEntity<Scenario> addScenarios(@NonNull @RequestBody NewScenario newScenario)
	{
		return ResponseEntity.of(scenarioService.addScenarios(newScenario));
	}

	@PutMapping("{id}") // allowed: user (author) & admin
	public ResponseEntity<Scenario> updateScenario(@NonNull @PathVariable String id, @NonNull @RequestBody Scenario scenario)
			throws UserIsNotAllowedException
	{
		return ResponseEntity.of(scenarioService.updateScenario(id, scenario));
	}

	@DeleteMapping("{id}") // allowed: user (author) & admin
	public void deleteScenario(@NonNull @PathVariable String id)
			throws UserIsNotAllowedException
	{
		scenarioService.deleteScenario(id);
	}

}
