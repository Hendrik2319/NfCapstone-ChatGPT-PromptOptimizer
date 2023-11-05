package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.*;

@RequiredArgsConstructor
class PromptGenerator {

	private final String prompt;
	private final List<String> variables;
	private final List<Map<String, List<String>>> testcases;

	interface PromptAction {
		void process(@NonNull String prompt, int indexOfTestCase, int totalAmountOfPrompts, @NonNull String label);
	}

	void foreachPrompt(@NonNull PromptAction action) {
		HashSet<String> usedVars = computeUsedVars();

		if (usedVars.isEmpty()) {
			action.process(prompt, -1, 1, "A Single Request");
			return;
		}

		int totalAmountOfPrompts = 0;
		for (Map<String, List<String>> testcase : testcases)
			totalAmountOfPrompts += computeAmountOfPrompts(testcase, usedVars);

		for (int i=0; i<testcases.size(); i++)
			foreachPrompt(totalAmountOfPrompts, i, testcases.get(i), usedVars, action);
	}

	private int computeAmountOfPrompts(Map<String, List<String>> testcase, HashSet<String> usedVars) {
		int amountOfPrompts = 1;

		for (String varName : usedVars) {
			List<String> values = testcase.get(varName);
			if (values==null || values.isEmpty())
				return 0; // no prompts for this testcase, because at least one used variable has no values defined
			amountOfPrompts *= values.size();
		}

		return amountOfPrompts;
	}

	private HashSet<String> computeUsedVars() {
		HashSet<String> usedVars = new HashSet<>();
		for (String varName : variables)
			if (prompt.contains("{%s}".formatted(varName)))
				usedVars.add(varName);
		return usedVars;
	}

	private void foreachPrompt(int totalAmountOfPrompts, int testcaseIndex, Map<String, List<String>> testcase, HashSet<String> usedVars, PromptAction action) {
		Comparator<String> ignoringCaseComparator = Comparator.<String, String>comparing(String::toLowerCase).thenComparing(Comparator.naturalOrder());

		Map<String, List<String>> valuesMap = new HashMap<>();
		for (String varName : usedVars) {
			List<String> values = testcase.get(varName);
			if (values==null || values.isEmpty())
				return; // do nothing, because at least one used variable has no values defined
			valuesMap.put(varName, values);
		}

		List<String> usedVarsList = usedVars
				.stream()
				.sorted(ignoringCaseComparator)
				.toList();

		runLoopsRecursive(0, usedVarsList, valuesMap, new HashMap<>(), values -> {
			String prompt = buildPrompt(values);
			String label = buildLabel(testcaseIndex, values, usedVarsList);
			action.process(prompt, testcaseIndex, totalAmountOfPrompts, label);
		});
	}

	private String buildLabel(int testcaseIndex, Map<String, String> values, List<String> usedVarsList) {
		StringBuilder sb = new StringBuilder();

		sb.append("TestCase %d {".formatted(testcaseIndex+1));
		for (String varName : usedVarsList)
			sb.append(" %s:\"%s\"".formatted(varName, values.get(varName)));
		sb.append(" }");

		return sb.toString();
	}

	private String buildPrompt(Map<String, String> values) {
		String promptStr = prompt;

		for (Map.Entry<String, String> entry : values.entrySet())
			promptStr = promptStr.replace("{%s}".formatted(entry.getKey()), entry.getValue());

		return promptStr;
	}

	private interface LoopAction {
		void doWith(Map<String,String> values);
	}

	private void runLoopsRecursive(
			int index,
			List<String> usedVarsList,
			Map<String, List<String>> valuesMap,
			Map<String,String> selectedValues,
			LoopAction action
	) {
		if (index >= usedVarsList.size()) {
			action.doWith(selectedValues);
			return;
		}

		String varName = usedVarsList.get(index);
		List<String> values = valuesMap.get(varName);

		for (String value : values) {
			selectedValues.put(varName, value);
			runLoopsRecursive(index+1, usedVarsList, valuesMap, selectedValues, action);
		}
	}
}
