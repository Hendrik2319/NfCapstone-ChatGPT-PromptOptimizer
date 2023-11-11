package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.Answer;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.services.ChatGptService;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.Prompt;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.NewTestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.Scenario;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.models.TestRun;
import net.schwarzbaer.spring.promptoptimizer.backend.prompttests.repositories.TestRunRepository;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestRunService {

	private final TestRunRepository testRunRepository;
	private final ScenarioService scenarioService;
	private final ChatGptService chatGptService;
	private final TimeService timeService;
	private final RunningTestRunsList runningTestRunsList;

	public List<TestRun> getTestRunsOfScenario(@NonNull String scenarioId)
			throws UserIsNotAllowedException
	{
		Optional<Scenario> storedScenarioOpt = scenarioService.getScenarioById(scenarioId);
		if (storedScenarioOpt.isEmpty()) return List.of();
		// if it's not empty user is allowed to have scenario access

		return testRunRepository.findAllByScenarioId(scenarioId);
	}

	public TestRun addTestRun(@NonNull String scenarioId, @NonNull TestRun testRun) throws UserIsNotAllowedException {
		if (testRun.id()!=null)
			throw new IllegalArgumentException("A new TestRun must not have an ID if it is to be added to the database.");
		if (!scenarioId.equals(testRun.scenarioId()))
			throw new IllegalArgumentException(
					"Given scenarioId (\""+scenarioId+"\") is different to"+
					" scenarioId (\""+testRun.scenarioId()+"\") in given TestRun"
			);

		Optional<Scenario> storedScenarioOpt = scenarioService.getScenarioById(scenarioId);
		if (storedScenarioOpt.isEmpty())
			throw new NoSuchElementException("Can't add TestRun, no Scenario with ID \"%s\" found.".formatted(scenarioId));
		// if it's not empty user is allowed to have scenario access

		return testRunRepository.save(testRun);
	}

	public void performTestRun(@NonNull NewTestRun newTestRun) throws UserIsNotAllowedException {
		String scenarioId = newTestRun.scenarioId();
		String prompt = newTestRun.prompt();
		List<String> variables = newTestRun.variables();
		List<Map<String, List<String>>> testcases = newTestRun.testcases();
		if (scenarioId==null) throw new IllegalArgumentException("A NewTestRun must have a (non null) scenario ID.");
		if (prompt    ==null) throw new IllegalArgumentException("A NewTestRun must have a (non null) prompt.");
		if (variables ==null) throw new IllegalArgumentException("A NewTestRun must have a (non null) list of variables.");
		if (testcases ==null) throw new IllegalArgumentException("A NewTestRun must have a (non null) list of testcases.");

		Optional<Scenario> storedScenarioOpt = scenarioService.getScenarioById(scenarioId);
		if (storedScenarioOpt.isEmpty())
			throw new NoSuchElementException("Can't perform a TestRun, no Scenario with ID \"%s\" found.".formatted(scenarioId));

		ZonedDateTime now = timeService.getNow();

		List<TestRun.TestAnswer> answers = new ArrayList<>();
		PromptGenerator generator = new PromptGenerator( prompt, variables, testcases );
		RunningTestRunsList.ListEntry listEntry = runningTestRunsList.createNewEntry(scenarioId);
		generator.foreachPrompt(
				(finalPrompt, indexOfTestCase, totalAmountOfPrompts, label) -> {
					listEntry.setValues(answers.size(), totalAmountOfPrompts, finalPrompt, label);
					log.info("+++++ performTestRun(): Next API request: [%d/%d] \"%s\"".formatted(
							listEntry.getPromptIndex(),
							listEntry.getTotalAmountOfPrompts(),
							listEntry.getLabel()
					));
					Answer answer = null;
					try {
						answer = chatGptService.askChatGPT(new Prompt(finalPrompt));
					} catch (Exception e) {
						log.error("+++++ %s while requesting ChatGPT API. [ Message: %s ]".formatted(e.getClass().getSimpleName(), e.getMessage()));
					}
					if (answer!=null) {
						answers.add(new TestRun.TestAnswer(
								indexOfTestCase,
								label,
								answer.answer(),
								answer.promptTokens(),
								answer.completionTokens(),
								answer.totalTokens()
						));
						log.info("+++++ performTestRun(): answer added");
					}
				}
		);
		runningTestRunsList.removeEntry(scenarioId, listEntry);

		Double averageTokensPerRequest = computeAverageTokensPerRequest(answers);

		testRunRepository.save(new TestRun(
				null, scenarioId, now,
				prompt,
				variables,
				testcases,
				answers,
				averageTokensPerRequest
		));
	}

	Double computeAverageTokensPerRequest(@NonNull List<TestRun.TestAnswer> answers) {

		int[] tokens = answers.stream()
				.filter(answer ->
						answer.promptTokens    () != null ||
						answer.completionTokens() != null ||
						answer.totalTokens     () != null
				)
				.mapToInt(answer -> {
					if (answer.totalTokens() != null)
						return answer.totalTokens();
					return
							(answer.promptTokens() != null ? answer.promptTokens() : 0) +
							(answer.completionTokens() != null ? answer.completionTokens() : 0);
				})
				.toArray();

		if (tokens.length==0)
			return null;

		return Arrays.stream(tokens).sum() / (double)tokens.length;
	}
}
