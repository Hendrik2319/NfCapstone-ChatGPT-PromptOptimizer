package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TestRunRepository extends MongoRepository<TestRun, String> {
	List<TestRun> findAllByScenarioId(@NonNull String scenarioId);
}
