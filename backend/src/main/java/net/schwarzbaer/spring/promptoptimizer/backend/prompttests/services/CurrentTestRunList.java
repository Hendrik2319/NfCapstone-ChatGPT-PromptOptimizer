package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CurrentTestRunList {

	private final Map<String, List<ListEntry>> currentTestRuns = new HashMap<>();

	Map<String, List<ListEntry>> getCurrentTestRunsForUnitTest() {
		return currentTestRuns;
	}

	ListEntry createNewEntryForUnitTest(int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
		return new ListEntry(promptIndex, totalAmountOfPrompts, prompt, label);
	}

	public synchronized ListEntry createNewEntry(String scenarioId) {
		List<ListEntry> entries = currentTestRuns.computeIfAbsent(scenarioId, key->new ArrayList<>());
		ListEntry listEntry = new ListEntry();
		entries.add(listEntry);
		return listEntry;
	}

	public synchronized List<ListEntry> getEntries(String scenarioId) {
		List<ListEntry> entries = currentTestRuns.get(scenarioId);
		return entries==null ? new ArrayList<>() : entries;
	}

	public synchronized void removeEntry(String scenarioId, ListEntry listEntry) {
		List<ListEntry> entries = currentTestRuns.get(scenarioId);
		if (entries!=null) {
			entries.remove(listEntry);
			if (entries.isEmpty())
				currentTestRuns.remove(scenarioId);
		}
	}

	@Getter
	public class ListEntry {

		private int promptIndex;
		private int totalAmountOfPrompts;
		private String prompt;
		private String label;

		private ListEntry() {
			this(-1, -1, null, null);
		}
		private ListEntry(int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
			this.promptIndex = promptIndex;
			this.totalAmountOfPrompts = totalAmountOfPrompts;
			this.prompt = prompt;
			this.label = label;
		}

		public void setValues(int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
			synchronized (CurrentTestRunList.this) {
				this.promptIndex = promptIndex;
				this.totalAmountOfPrompts = totalAmountOfPrompts;
				this.prompt = prompt;
				this.label = label;
			}
		}
	}
}
