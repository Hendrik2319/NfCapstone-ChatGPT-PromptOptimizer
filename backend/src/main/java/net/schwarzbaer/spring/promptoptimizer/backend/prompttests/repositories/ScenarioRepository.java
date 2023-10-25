package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScenarioRepository  extends MongoRepository<Scenario, String> {

	List<Scenario> findByAuthorID(String authorID);
}
