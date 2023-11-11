package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.Getter;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RunningTestRunsList {

	private final Map<String, List<ListEntry>> runningTestRuns = new HashMap<>();

	Map<String, List<ListEntry>> getDataForUnitTest() {
		return runningTestRuns;
	}

	@NonNull
	public synchronized ListEntry createNewEntry(@NonNull String scenarioId) {
		List<ListEntry> entries = runningTestRuns.computeIfAbsent(scenarioId, key->new ArrayList<>());
		ListEntry listEntry = new ListEntry(this);
		entries.add(listEntry);
		return listEntry;
	}

	public synchronized List<ListEntryDTO> getEntries(@NonNull String scenarioId) {
		List<ListEntry> entries = runningTestRuns.get(scenarioId);
		return entries==null ? List.of() : entries.stream().map(ListEntry::getDTO).toList();
	}

	public synchronized void removeEntry(@NonNull String scenarioId, @NonNull ListEntry listEntry) {
		List<ListEntry> entries = runningTestRuns.get(scenarioId);
		if (entries!=null) {
			entries.remove(listEntry);
			if (entries.isEmpty())
				runningTestRuns.remove(scenarioId);
		}
	}

	@Getter
	public static class ListEntry {

		private final @NonNull RunningTestRunsList container;
		private int promptIndex;
		private int totalAmountOfPrompts;
		private String prompt;
		private String label;

		private ListEntry(@NonNull RunningTestRunsList container) {
			this(container,-1, -1, null, null);
		}
		ListEntry(@NonNull RunningTestRunsList container, int promptIndex, int totalAmountOfPrompts, String prompt, String label) {
			this.container = container;
			this.promptIndex = promptIndex;
			this.totalAmountOfPrompts = totalAmountOfPrompts;
			this.prompt = prompt;
			this.label = label;
		}

		public void setValues(int promptIndex, int totalAmountOfPrompts, @NonNull String prompt, @NonNull String label) {
			synchronized (container) {
				this.promptIndex = promptIndex;
				this.totalAmountOfPrompts = totalAmountOfPrompts;
				this.prompt = prompt;
				this.label = label;
			}
		}

		private ListEntryDTO getDTO() {
			return new ListEntryDTO(
					promptIndex,
					totalAmountOfPrompts,
					prompt,
					label
			);
		}
	}

	public record ListEntryDTO(
			int promptIndex,
			int totalAmountOfPrompts,
			String prompt,
			String label
	) {}
}
