package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PromptGeneratorTest {

	private PromptGenerator promptGenerator;

	private record PromptActionCall(@NonNull String prompt, int indexOfTestCase, int totalAmountOfPrompts, @NonNull String label) {}

	@Test
	void whenForeachPrompt_getsPromptWithoutVariables() {
		// Given
		promptGenerator = new PromptGenerator(
				"Text Text Text",
				List.of("var1","var2"),
				List.of(
						Map.of(
								"var1", List.of("value1.1", "value1.2", "value1.3"),
								"var2", List.of("value2.1", "value2.2")
						),
						Map.of(
								"var1", List.of("value1.4"),
								"var2", List.of("value2.3", "value2.4")
						)
				)
		);

		// When
		ArrayList<PromptActionCall> actual = new ArrayList<>();
		promptGenerator.foreachPrompt((prompt, indexOfTestCase, totalAmountOfPrompts, label) ->
				actual.add( new PromptActionCall(prompt, indexOfTestCase, totalAmountOfPrompts, label) )
		);

		// Then
		List<Object> expected = List.of(
				new PromptActionCall("Text Text Text", -1, 1, "A Single Request")
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenForeachPrompt_getsPrompt() {
		// Given
		promptGenerator = new PromptGenerator(
				"{var1}##{var2}",
				List.of("var1","var2"),
				List.of(
						Map.of(
								"var1", List.of("value1.1", "value1.2", "value1.3"),
								"var2", List.of("value2.1", "value2.2")
						),
						Map.of(
								"var1", List.of("value1.4"),
								"var2", List.of("value2.3", "value2.4")
						)
				)
		);

		// When
		ArrayList<PromptActionCall> actual = new ArrayList<>();
		promptGenerator.foreachPrompt((prompt, indexOfTestCase, totalAmountOfPrompts, label) ->
				actual.add( new PromptActionCall(prompt, indexOfTestCase, totalAmountOfPrompts, label) )
		);

		// Then
		int totalAmountOfPrompts = 8;
		List<Object> expected = List.of(
				new PromptActionCall("value1.1##value2.1", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.1\" var2:\"value2.1\" }"),
				new PromptActionCall("value1.1##value2.2", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.1\" var2:\"value2.2\" }"),
				new PromptActionCall("value1.2##value2.1", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.2\" var2:\"value2.1\" }"),
				new PromptActionCall("value1.2##value2.2", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.2\" var2:\"value2.2\" }"),
				new PromptActionCall("value1.3##value2.1", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.3\" var2:\"value2.1\" }"),
				new PromptActionCall("value1.3##value2.2", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.3\" var2:\"value2.2\" }"),
				new PromptActionCall("value1.4##value2.3", 1, totalAmountOfPrompts, "TestCase 2 { var1:\"value1.4\" var2:\"value2.3\" }"),
				new PromptActionCall("value1.4##value2.4", 1, totalAmountOfPrompts, "TestCase 2 { var1:\"value1.4\" var2:\"value2.4\" }")
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenForeachPrompt_getsPromptWithMultipleOccurrencesOfVariable() {
		// Given
		promptGenerator = new PromptGenerator(
				"{var2}~~{var1}~~ {var1} {var2}~~",
				List.of("var1","var2"),
				List.of(
						Map.of(
								"var1", List.of("value1.1", "value1.2", "value1.3"),
								"var2", List.of("value2.1", "value2.2")
						),
						Map.of(
								"var1", List.of("value1.4"),
								"var2", List.of("value2.3", "value2.4")
						)
				)
		);

		// When
		ArrayList<PromptActionCall> actual = new ArrayList<>();
		promptGenerator.foreachPrompt((prompt, indexOfTestCase, totalAmountOfPrompts, label) ->
				actual.add( new PromptActionCall(prompt, indexOfTestCase, totalAmountOfPrompts, label) )
		);

		// Then
		int totalAmountOfPrompts = 8;
		List<Object> expected = List.of(
				new PromptActionCall("value2.1~~value1.1~~ value1.1 value2.1~~", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.1\" var2:\"value2.1\" }"),
				new PromptActionCall("value2.2~~value1.1~~ value1.1 value2.2~~", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.1\" var2:\"value2.2\" }"),
				new PromptActionCall("value2.1~~value1.2~~ value1.2 value2.1~~", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.2\" var2:\"value2.1\" }"),
				new PromptActionCall("value2.2~~value1.2~~ value1.2 value2.2~~", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.2\" var2:\"value2.2\" }"),
				new PromptActionCall("value2.1~~value1.3~~ value1.3 value2.1~~", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.3\" var2:\"value2.1\" }"),
				new PromptActionCall("value2.2~~value1.3~~ value1.3 value2.2~~", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.3\" var2:\"value2.2\" }"),
				new PromptActionCall("value2.3~~value1.4~~ value1.4 value2.3~~", 1, totalAmountOfPrompts, "TestCase 2 { var1:\"value1.4\" var2:\"value2.3\" }"),
				new PromptActionCall("value2.4~~value1.4~~ value1.4 value2.4~~", 1, totalAmountOfPrompts, "TestCase 2 { var1:\"value1.4\" var2:\"value2.4\" }")
		);
		assertEquals(expected, actual);
	}

	@Test
	void whenForeachPrompt_getsPromptWithTestcaseWithAnEmptyValuesList() {
		// Given
		HashMap<String, List<String>> testcase3 = new HashMap<>();
		testcase3.put("var1", null);
		testcase3.put("var2", List.of("value2.5"));
		promptGenerator = new PromptGenerator(
				"{var1}##{var2}",
				List.of("var1","var2"),
				List.of(
						Map.of(
								"var1", List.of("value1.1", "value1.2", "value1.3"),
								"var2", List.of("value2.1", "value2.2")
						),
						Map.of(
								"var1", List.of(),
								"var2", List.of("value2.3", "value2.4")
						),
						testcase3
				)
		);

		// When
		ArrayList<PromptActionCall> actual = new ArrayList<>();
		promptGenerator.foreachPrompt((prompt, indexOfTestCase, totalAmountOfPrompts, label) ->
				actual.add( new PromptActionCall(prompt, indexOfTestCase, totalAmountOfPrompts, label) )
		);

		// Then
		int totalAmountOfPrompts = 6;
		List<Object> expected = List.of(
				new PromptActionCall("value1.1##value2.1", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.1\" var2:\"value2.1\" }"),
				new PromptActionCall("value1.1##value2.2", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.1\" var2:\"value2.2\" }"),
				new PromptActionCall("value1.2##value2.1", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.2\" var2:\"value2.1\" }"),
				new PromptActionCall("value1.2##value2.2", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.2\" var2:\"value2.2\" }"),
				new PromptActionCall("value1.3##value2.1", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.3\" var2:\"value2.1\" }"),
				new PromptActionCall("value1.3##value2.2", 0, totalAmountOfPrompts, "TestCase 1 { var1:\"value1.3\" var2:\"value2.2\" }")
		);
		assertEquals(expected, actual);
	}
}