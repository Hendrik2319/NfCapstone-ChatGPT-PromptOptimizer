package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.ErrorMessage;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.ScenarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/scenario")
@RequiredArgsConstructor
@Slf4j
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

	@PostMapping
	public ResponseEntity<Scenario> addScenarios(@RequestBody NewScenario newScenario)
	{
		return ResponseEntity.of(scenarioService.addScenarios(newScenario));
	}

	@PutMapping("{id}")
	public ResponseEntity<Scenario> updateScenario(@PathVariable String id,@RequestBody Scenario scenario)
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

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(IllegalArgumentException ex)
	{
		return getErrorMessageAndDoLog("IllegalArgumentException", ex);
	}

	@ExceptionHandler(ScenarioService.UserIsNotAllowedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage handleException(ScenarioService.UserIsNotAllowedException ex)
	{
		return getErrorMessageAndDoLog("UserIsNotAllowedException", ex);
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(NoSuchElementException ex)
	{
		return getErrorMessageAndDoLog("NoSuchElementException", ex);
	}

	private static ErrorMessage getErrorMessageAndDoLog(String exception, Exception ex)
	{
		String message = "%s: %s".formatted(exception, ex.getMessage());
		log.error(message);
		return new ErrorMessage(message);
	}

}
