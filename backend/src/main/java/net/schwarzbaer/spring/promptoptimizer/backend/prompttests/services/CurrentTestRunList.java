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

	public synchronized ListEntry createNewEntry(String scenarioId) {
		List<ListEntry> entries = currentTestRuns.computeIfAbsent(scenarioId, key->new ArrayList<>());
		ListEntry listEntry = new ListEntry(this);
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
	public static class ListEntry {

		private final CurrentTestRunList container;
		private int promptIndex;
		private int totalAmountOfPrompts;
		private String prompt;
		private String label;

		ListEntry(CurrentTestRunList container) {
			this(container,-1, -1, null, null);
		}
		ListEntry(CurrentTestRunList container, int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
			this.container = container;
			this.promptIndex = promptIndex;
			this.totalAmountOfPrompts = totalAmountOfPrompts;
			this.prompt = prompt;
			this.label = label;
		}

		public void setValues(int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
			synchronized (container) {
				this.promptIndex = promptIndex;
				this.totalAmountOfPrompts = totalAmountOfPrompts;
				this.prompt = prompt;
				this.label = label;
			}
		}
	}
}
