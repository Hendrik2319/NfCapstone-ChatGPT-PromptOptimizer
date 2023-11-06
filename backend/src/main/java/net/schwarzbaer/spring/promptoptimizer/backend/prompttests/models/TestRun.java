package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record TestRun(
		String id,
		String scenarioId,
		ZonedDateTime timestamp,
		String prompt,
		List<String> variables,
		List<Map<String,List<String>>> testcases,
		List<TestAnswer> answers,
		Double averageTokensPerRequest
) {
	public record TestAnswer(
			int indexOfTestCase,
			String label,
			String answer,
			Integer promptTokens,
			Integer completionTokens,
			Integer totalTokens
	) {}
}
