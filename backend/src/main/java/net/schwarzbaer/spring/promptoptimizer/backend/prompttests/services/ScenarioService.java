package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.UserService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScenarioService {

	private final ScenarioRepository scenarioRepository;
	private final UserService userService;

	public List<Scenario> getAllScenarios() {
		return scenarioRepository.findAll();
	}

	public List<Scenario> getAllScenariosOfUser() {
		UserInfos currentUser = userService.getCurrentUser();
		if (currentUser.userDbId()==null) return List.of();
		return scenarioRepository.findByAuthorID(currentUser.userDbId());
	}

	public Optional<Scenario> addScenarios(@NonNull NewScenario newScenario) {
		UserInfos currentUser = userService.getCurrentUser();
		if (currentUser.userDbId()==null) return Optional.empty();
		return Optional.of(scenarioRepository.save(new Scenario( currentUser.userDbId(), newScenario )));
	}

	public Optional<Scenario> updateScenario(@NonNull String id, @NonNull Scenario scenario) throws UserIsNotAllowedException {
		if ( scenario.id()      ==null) throw new IllegalArgumentException("Scenario have no [id]");
		if ( scenario.authorID()==null) throw new IllegalArgumentException("Scenario have no [authorID]");
		if (!scenario.id().equals(id) ) throw new IllegalArgumentException("Scenario have an [id] different to path variable");

		Optional<Scenario> storedScenarioOpt = scenarioRepository.findById(id);
		if (storedScenarioOpt.isEmpty()) return Optional.empty();
		Scenario storedScenario = storedScenarioOpt.get();

		UserInfos currentUser = userService.getCurrentUser();
		if (!currentUser.isAdmin()) {
			if (currentUser.userDbId()==null)
				throw new UserIsNotAllowedException("Current user has no [userDbId]");
			if (!currentUser.userDbId().equals(storedScenario.authorID()) ||
				!currentUser.userDbId().equals(scenario.authorID()))
				throw new UserIsNotAllowedException("Current user is not allowed to update a Scenario of another user.");
		}

		return Optional.of(scenarioRepository.save(scenario));
	}

	public static class UserIsNotAllowedException extends Exception {
		public UserIsNotAllowedException(String message) {
			super(message);
		}
	}
}
