package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.PrintStream;
import java.util.*;

@RequiredArgsConstructor
class PromptGenerator {

	private static final PrintStream DEBUG_OUT = System.out;

	private final String prompt;
	private final List<String> variables;
	private final List<Map<String, List<String>>> testcases;
	private final HashSet<String> usedVars = new HashSet<>();

	interface PromptAction {
		void process(@NonNull String prompt, int indexOfTestCase, @NonNull String label);
	}
	private record Part(@NonNull String before, @Nullable String varName) {}

	void foreachPrompt(@NonNull PromptAction action) {
		List<Part> parts = getParts();


		DEBUG_OUT.printf("UsedVars: %s%n", usedVars);
		DEBUG_OUT.printf("Parts: [%d]%n", parts.size() * 2 - 1);
		for (int i=0; i<parts.size(); i++) {
			Part part = parts.get(i);
			if (part.varName==null)
				DEBUG_OUT.printf("\"%s\" ", part.before);
			else
				DEBUG_OUT.printf("\"%s\" <%s> ", part.before, part.varName);
		}
		DEBUG_OUT.println();


		for (int i=0; i<testcases.size(); i++)
			foreachPrompt(i, testcases.get(i), parts, action);
	}

	private void foreachPrompt(int testcaseIndex, Map<String, List<String>> testcase, List<Part> parts, PromptAction action) {
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
			String prompt = buildPrompt(parts, values);
			String label = buildLabel(testcaseIndex, values, usedVarsList);
			action.process(prompt, testcaseIndex, label);
			DEBUG_OUT.printf("[%d] | \"%s\" | \"%s\"%n", testcaseIndex, label, prompt);
		});
	}

	private String buildLabel(int testcaseIndex, Map<String, String> values, List<String> usedVarsList) {
		StringBuilder sb = new StringBuilder();

		sb.append("[%d]".formatted(testcaseIndex));
		for (String varName : usedVarsList)
			sb.append(" %s:\"%s\"".formatted(varName, values.get(varName)));

		return sb.toString();
	}

	private String buildPrompt(List<Part> parts, Map<String, String> values) {
		StringBuilder sb = new StringBuilder();

		for (Part part : parts) {
			sb.append(part.before);
			if (part.varName!=null) {
				String value = values.get(part.varName);
				if (value==null)
					throw new IllegalStateException("[Unexpected State] Can't find variable value: { parts:%s, values:%s }".formatted(parts, values));
				sb.append(value);
			}
		}

		return sb.toString();
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

	private List<Part> getParts() {
		ArrayList<Part> parts = new ArrayList<>();

		String promptStr = this.prompt;
		while (!promptStr.isEmpty())
		{
			int nearestVarPos = -1;
			int nearestVarIndex = -1;

			for (int i=0; i<variables.size(); i++)
			{
				String variable = variables.get(i);
				String placeholder = "{%s}".formatted(variable);
				int pos = promptStr.indexOf(placeholder);
				if (pos < 0) continue;
				if (nearestVarPos < 0 || pos < nearestVarPos)
				{
					nearestVarPos = pos;
					nearestVarIndex = i;
				}
			}

			if (nearestVarPos < 0)
			{ // no var found
				parts.add(new Part(promptStr, null ));
				promptStr = "";
			}
			else
			{ // nearest var found at {nextVarPos}
				String variable = variables.get(nearestVarIndex);
				String placeholder = "{%s}".formatted(variable);

				usedVars.add( variable );
				parts.add(new Part(
						promptStr.substring(0,nearestVarPos),
						variable
				));

				promptStr = promptStr.substring( nearestVarPos + placeholder.length() );
			}
		}

		return parts;
	}

}
