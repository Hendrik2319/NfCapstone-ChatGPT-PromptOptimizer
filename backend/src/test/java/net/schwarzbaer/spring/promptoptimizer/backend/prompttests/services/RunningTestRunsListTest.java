package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services.RunningTestRunsList.ListEntry;

class RunningTestRunsListTest {

	private RunningTestRunsList runningTestRunsList;
	private Map<String, List<RunningTestRunsList.ListEntry>> runningTestRuns;

	@BeforeEach
	void setup() {
		runningTestRunsList = new RunningTestRunsList();
		runningTestRuns = runningTestRunsList.getDataForUnitTest();
	}

	@NonNull
	private RunningTestRunsList.ListEntry addEntry(@NonNull String scenarioId, int promptIndex, int totalAmountOfPrompts, @NonNull String prompt, @NonNull String label) {
		RunningTestRunsList.ListEntry entry = runningTestRunsList.createNewEntry(scenarioId);
		entry.setValues(promptIndex, totalAmountOfPrompts, prompt, label);
		return entry;
	}

	private void assertEntryEquals(RunningTestRunsList.ListEntry entry, int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
		assertNotNull(entry);
		assertEquals(promptIndex         , entry.getPromptIndex         (), "promptIndex"         );
		assertEquals(totalAmountOfPrompts, entry.getTotalAmountOfPrompts(), "totalAmountOfPrompts");
		assertEquals(prompt              , entry.getPrompt              (), "prompt"              );
		assertEquals(label               , entry.getLabel               (), "label"               );
	}

	private void assertEntryEquals(RunningTestRunsList.ListEntryDTO entry, int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
		assertNotNull(entry);
		assertEquals(promptIndex         , entry.promptIndex         (), "promptIndex"         );
		assertEquals(totalAmountOfPrompts, entry.totalAmountOfPrompts(), "totalAmountOfPrompts");
		assertEquals(prompt              , entry.prompt              (), "prompt"              );
		assertEquals(label               , entry.label               (), "label"               );
	}

	@Test
	void whenCreateNewEntry() {
		// Given

		// When
		RunningTestRunsList.ListEntry actual = runningTestRunsList.createNewEntry("scenarioId1");

		// Then
		List<RunningTestRunsList.ListEntry> entries = runningTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(1, entries.size());

		RunningTestRunsList.ListEntry storedEntry = entries.get(0);
		assertNotNull(storedEntry);
		assertEntryEquals(storedEntry, -1, -1, null, null);

		assertNotNull(actual);
		assertEntryEquals(actual, -1, -1, null, null);
	}

	@Test
	void whenSetValues() {
		// Given
		RunningTestRunsList.ListEntry entry = runningTestRunsList.createNewEntry("scenarioId1");

		// When
		entry.setValues(2, 5, "prompt", "label");

		// Then
		List<RunningTestRunsList.ListEntry> entries = runningTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(1, entries.size());

		RunningTestRunsList.ListEntry storedEntry = entries.get(0);
		assertEntryEquals(storedEntry,2, 5, "prompt", "label");
	}

	@Test
	void whenGetEntries_isCalledWithFilledList_returnsList() {
		// Given
		addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");
		addEntry("scenarioId2", 5, 5, "prompt2" , "label2" );

		// When
		List<RunningTestRunsList.ListEntryDTO> actual = runningTestRunsList.getEntries("scenarioId1");

		// Then
		assertNotNull(actual);
		assertEquals(2, actual.size());

		RunningTestRunsList.ListEntryDTO entry0 = actual.get(0);
		RunningTestRunsList.ListEntryDTO entry1 = actual.get(1);
		assertEntryEquals(entry0,2, 5, "prompt1a", "label1a");
		assertEntryEquals(entry1,3, 4, "prompt1b", "label1b");
	}

	@Test
	void whenGetEntries_isCalledWithEmptyListOrUnknownScenarioId_returnsEmptyList() {
		// Given

		// When
		List<RunningTestRunsList.ListEntryDTO> actual = runningTestRunsList.getEntries("scenarioId1");

		// Then
		List<RunningTestRunsList.ListEntryDTO> expected = List.of();
		assertEquals(expected, actual);
	}

	@Test
	void whenRemoveEntry_isCalledWithCorrespondingEntry() {
		// Given
		RunningTestRunsList.ListEntry entry1a =
				addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");

		// When
		runningTestRunsList.removeEntry("scenarioId1", entry1a);

		// Then
		List<RunningTestRunsList.ListEntry> entries = runningTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(1, entries.size());

		RunningTestRunsList.ListEntry remainingEntry = entries.get(0);
		assertEntryEquals(remainingEntry,3, 4, "prompt1b", "label1b");
	}

	@Test
	void whenRemoveEntry_isCalledWithLastEntry() {
		// Given
		RunningTestRunsList.ListEntry entry1a = addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");

		// When
		runningTestRunsList.removeEntry("scenarioId1", entry1a);

		// Then
		List<RunningTestRunsList.ListEntry> entries = runningTestRuns.get("scenarioId1");
		assertNull(entries);
	}

	@Test
	void whenRemoveEntry_isCalledWithUnknownScenarioId() {
		// Given
		ListEntry entry =
			addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");

		// When
		runningTestRunsList.removeEntry("scenarioId2", entry);

		// Then
		List<RunningTestRunsList.ListEntry> entries = runningTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(2, entries.size());

		RunningTestRunsList.ListEntry entry0 = entries.get(0);
		RunningTestRunsList.ListEntry entry1 = entries.get(1);
		assertEntryEquals(entry0,2, 5, "prompt1a", "label1a");
		assertEntryEquals(entry1,3, 4, "prompt1b", "label1b");
	}

	@Test
	void whenRemoveEntry_isCalledWithUnknownEntry() {
		// Given
		addEntry("scenarioId1", 2, 5, "prompt1a", "label1a");
		addEntry("scenarioId1", 3, 4, "prompt1b", "label1b");
		RunningTestRunsList.ListEntry unknownEntry =
				new RunningTestRunsList.ListEntry( Objects.requireNonNull( runningTestRunsList ), 2, 5, "prompt1a", "label1a");

		// When
		runningTestRunsList.removeEntry("scenarioId1", unknownEntry);

		// Then
		List<RunningTestRunsList.ListEntry> entries = runningTestRuns.get("scenarioId1");
		assertNotNull(entries);
		assertEquals(2, entries.size());

		RunningTestRunsList.ListEntry entry0 = entries.get(0);
		RunningTestRunsList.ListEntry entry1 = entries.get(1);
		assertEntryEquals(entry0,2, 5, "prompt1a", "label1a");
		assertEntryEquals(entry1,3, 4, "prompt1b", "label1b");
	}
}