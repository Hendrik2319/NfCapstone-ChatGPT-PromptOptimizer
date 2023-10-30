package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models;

import java.util.List;
import java.util.Map;

public record NewTestRun(
//		String id,    - defined by database
		String scenarioId,
//		ZonedDateTime timestamp,   - defined by backend
		String prompt,
		List<String> variables,
		List<Map<String,List<String>>> testcases
//		List<TestAnswer> answers  - defined by backend as results from external API
) {
}
