package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrentTestRunList {

	private final Map<String, List<ListEntry>> currentTestRuns = new HashMap<>();

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
		if (entries!=null)
			entries.remove(listEntry);
	}

	@Getter
	public class ListEntry {

		private int promptIndex = -1;
		private int totalAmountOfPrompts = -1;
		private String prompt = null;
		private String label = null;

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
