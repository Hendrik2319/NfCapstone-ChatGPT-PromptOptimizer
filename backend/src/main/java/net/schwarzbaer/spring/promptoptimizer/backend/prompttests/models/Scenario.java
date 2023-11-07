package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models;

import org.springframework.lang.NonNull;

public record Scenario(
		String id,
		String authorID,
		String label,
		Integer maxWantedWordCount
) {
	public Scenario(@NonNull String authorID, @NonNull NewScenario newScenario) {
		this(
				null,
				authorID,
				newScenario.label(),
				null
		);
	}
}
