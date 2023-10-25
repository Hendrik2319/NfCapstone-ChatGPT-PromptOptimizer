package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models;

public record Scenario(
		String id,
		String authorID,
		String label
) {
	public Scenario(NewScenario newScenario) {
		this(
				null,
				newScenario.authorID(),
				newScenario.label()
		);
	}
}
