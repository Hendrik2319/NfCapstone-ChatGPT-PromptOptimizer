package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CurrentTestRunListTest {

	private CurrentTestRunList currentTestRunList;
	private Map<String, List<CurrentTestRunList.ListEntry>> currentTestRuns;

	@BeforeEach
	void setup() {
		currentTestRunList = new CurrentTestRunList();
		currentTestRuns = currentTestRunList.getCurrentTestRunsForUnitTest();
	}

	private CurrentTestRunList.ListEntry addEntry(String scenarioId, int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
		CurrentTestRunList.ListEntry entry = currentTestRunList.createNewEntry(scenarioId);
		entry.setValues(promptIndex, totalAmountOfPrompts, prompt, label);
		return entry;
	}

	private void assertEntryEquals(CurrentTestRunList.ListEntry entry, int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
		assertNotNull(entry);
		assertEquals(promptIndex         , entry.getPromptIndex         (), "promptIndex"         );
		assertEquals(totalAmountOfPrompts, entry.getTotalAmountOfPrompts(), "totalAmountOfPrompts");
		assertEquals(prompt              , entry.getPrompt              (), "prompt"              );
		assertEquals(label               , entry.getLabel               (), "label"               );
	}

	@Test
	void whenCreateNewEntry() {
		// Given

		// When
		CurrentTestRunList.ListEntry actual = currentTestRunList.createNewEntry("scenarioId1");

		// Then
		List<CurrentTestRunList.ListEntry> entries = currentTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(1, entries.size());

		CurrentTestRunList.ListEntry storedEntry = entries.get(0);
		assertNotNull(storedEntry);
		assertEntryEquals(storedEntry, -1, -1, null, null);

		assertNotNull(actual);
		assertEntryEquals(actual, -1, -1, null, null);
	}

	@Test
	void whenSetValues() {
		// Given
		CurrentTestRunList.ListEntry entry = currentTestRunList.createNewEntry("scenarioId1");

		// When
		entry.setValues(2, 5, "prompt", "label");

		// Then
		List<CurrentTestRunList.ListEntry> entries = currentTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(1, entries.size());

		CurrentTestRunList.ListEntry storedEntry = entries.get(0);
		assertEntryEquals(storedEntry,2, 5, "prompt", "label");
	}

	@Test
	void whenGetEntries_isCalledWithFilledList_returnsList() {
		// Given
		addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");
		addEntry("scenarioId2", 5, 5, "prompt2" , "label2" );

		// When
		List<CurrentTestRunList.ListEntry> actual = currentTestRunList.getEntries("scenarioId1");

		// Then
		assertNotNull(actual);
		assertEquals(2, actual.size());

		CurrentTestRunList.ListEntry entry0 = actual.get(0);
		CurrentTestRunList.ListEntry entry1 = actual.get(1);
		assertEntryEquals(entry0,2, 5, "prompt1a", "label1a");
		assertEntryEquals(entry1,3, 4, "prompt1b", "label1b");
	}

	@Test
	void whenGetEntries_isCalledWithEmptyListOrUnknownScenarioId_returnsEmptyList() {
		// Given

		// When
		List<CurrentTestRunList.ListEntry> actual = currentTestRunList.getEntries("scenarioId1");

		// Then
		List<CurrentTestRunList.ListEntry> expected = List.of();
		assertEquals(expected, actual);
	}

	@Test
	void whenRemoveEntry_isCalledWithCorrespondingEntry() {
		// Given
		CurrentTestRunList.ListEntry entry1a =
				addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");

		// When
		currentTestRunList.removeEntry("scenarioId1", entry1a);

		// Then
		List<CurrentTestRunList.ListEntry> entries = currentTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(1, entries.size());

		CurrentTestRunList.ListEntry remainingEntry = entries.get(0);
		assertEntryEquals(remainingEntry,3, 4, "prompt1b", "label1b");
	}

	@Test
	void whenRemoveEntry_isCalledWithLastEntry() {
		// Given
		CurrentTestRunList.ListEntry entry1a = addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");

		// When
		currentTestRunList.removeEntry("scenarioId1", entry1a);

		// Then
		List<CurrentTestRunList.ListEntry> entries = currentTestRuns.get("scenarioId1");
		assertNull(entries);
	}

	@Test
	void whenRemoveEntry_isCalledWithUnknownEntry() {
		// Given
		addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");
		CurrentTestRunList.ListEntry unknownEntry =
				new CurrentTestRunList.ListEntry(currentTestRunList, 2, 5, "prompt1a", "label1a");

		// When
		currentTestRunList.removeEntry("scenarioId1", unknownEntry);

		// Then
		List<CurrentTestRunList.ListEntry> entries = currentTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(2, entries.size());

		CurrentTestRunList.ListEntry entry0 = entries.get(0);
		CurrentTestRunList.ListEntry entry1 = entries.get(1);
		assertEntryEquals(entry0,2, 5, "prompt1a", "label1a");
		assertEntryEquals(entry1,3, 4, "prompt1b", "label1b");
	}
}