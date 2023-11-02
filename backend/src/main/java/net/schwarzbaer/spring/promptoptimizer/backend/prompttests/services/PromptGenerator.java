package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;

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
		void process(String prompt, int indexOfTestCase, String label);
	}
	private record Part(String before, String varName) {}

	void foreachPrompt(PromptAction action) {
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


		for (int i=0; i<testcases.size(); i++) {
			Map<String, List<String>> testcase = testcases.get(i);

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
