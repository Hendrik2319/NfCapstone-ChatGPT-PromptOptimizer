package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewScenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.ScenarioRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserService;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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
		UserInfo currentUser = userService.getCurrentUser();
		if (currentUser.userDbId()==null) return List.of();
		return scenarioRepository.findByAuthorID(currentUser.userDbId());
	}

	public Optional<Scenario> addScenarios(@NonNull NewScenario newScenario) {
		UserInfo currentUser = userService.getCurrentUser();
		if (currentUser.userDbId()==null) return Optional.empty();
		return Optional.of(scenarioRepository.save(new Scenario( currentUser.userDbId(), newScenario )));
	}

	public void deleteScenario(@NonNull String id) throws UserIsNotAllowedException {
		Optional<Scenario> storedScenarioOpt = scenarioRepository.findById(id);
		if (storedScenarioOpt.isEmpty()) throw new NoSuchElementException("Can't delete, No Scenario with ID \"%s\" found.".formatted(id));
		Scenario storedScenario = storedScenarioOpt.get();

		checkAuthorIDs("delete", storedScenario.authorID());

		scenarioRepository.deleteById(id);
	}

	public Optional<Scenario> updateScenario(@NonNull String id, @NonNull Scenario scenario) throws UserIsNotAllowedException {
		if ( scenario.id()      ==null) throw new IllegalArgumentException("Scenario have no [id]");
		if ( scenario.authorID()==null) throw new IllegalArgumentException("Scenario have no [authorID]");
		if (!scenario.id().equals(id) ) throw new IllegalArgumentException("Scenario have an [id] different to path variable");

		Optional<Scenario> storedScenarioOpt = scenarioRepository.findById(id);
		if (storedScenarioOpt.isEmpty()) return Optional.empty();
		Scenario storedScenario = storedScenarioOpt.get();

		checkAuthorIDs("update", storedScenario.authorID(), scenario.authorID());

		return Optional.of(scenarioRepository.save(scenario));
	}

	public Optional<Scenario> getScenarioById(@NonNull String id) throws UserIsNotAllowedException {
		Optional<Scenario> storedScenarioOpt = scenarioRepository.findById(id);
		if (storedScenarioOpt.isEmpty()) return Optional.empty();
		Scenario storedScenario = storedScenarioOpt.get();

		checkAuthorIDs("get", storedScenario.authorID());

		return Optional.of(storedScenario);
	}

	private void checkAuthorIDs(@NonNull String action, @NonNull String authorID) throws UserIsNotAllowedException {
		checkAuthorIDs(action, authorID, null);
	}
	private void checkAuthorIDs(@NonNull String action, @NonNull String authorID1, @Nullable String authorID2) throws UserIsNotAllowedException {
		UserInfo currentUser = userService.getCurrentUser();
		if (currentUser.isUser()) {
			if (currentUser.userDbId()==null)
				throw new UserIsNotAllowedException("Current user has no [userDbId]");
			if (!currentUser.userDbId().equals(authorID1) ||
					(authorID2 !=null && !currentUser.userDbId().equals(authorID2)) )
				throw new UserIsNotAllowedException("Current user is not allowed to "+ action +" a Scenario of another user.");
		} else
		if (!currentUser.isAdmin())
			throw new UserIsNotAllowedException("Current user is not allowed to "+ action +" a Scenario: Only Users and Admins are allowed.");
	}

}
