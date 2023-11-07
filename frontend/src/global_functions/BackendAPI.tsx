import axios, {AxiosResponse} from "axios";
import {NewScenario, Scenario} from "../models/ScenarioTypes.tsx";
import {NewTestRun, RunningTestRun, TestRun, TestRunDTO} from "../models/TestRunTypes.tsx";
import {convertNewTestRunIntoDTO, convertTestRunsFromDTOs} from "./TestRunFunctions.tsx";
import {Answer, ApiState, Prompt, UserInfos} from "../models/BaseTypes.tsx";

export const BackendAPI = {

    loadScenarioById: (scenarioId: string, callerLabel: string, callback: (scenario: Scenario) => void) =>
        processPromise(
            axios.get(`/api/scenario/${scenarioId}`),
            "loadScenarioById",
            `loading scenario (id:${scenarioId})`,
            callerLabel,
            callback
        ),

    loadTestRunsOfScenario: (scenarioId: string, callerLabel: string, callback: (testruns: TestRun[]) => void) =>
        processPromise(
            axios.get(`/api/scenario/${scenarioId}/testrun`),
            "loadTestRunsOfScenario",
            "loading test runs",
            callerLabel,
            (data: TestRunDTO[]) => callback(convertTestRunsFromDTOs(data))
        ),

    performTestRun: (newTestRun: NewTestRun, callerLabel: string, onSuccess: () => void) =>
        processPromise(
            axios.post(`/api/testrun`, convertNewTestRunIntoDTO(newTestRun)),
            "performTestRun",
            "performing a test run",
            callerLabel,
            onSuccess
        ),

    loadRunningTestRunsOfScenario: (scenarioId: string, callerLabel: string, callback: (runningTestRuns: RunningTestRun[]) => void) =>
        processPromise(
            axios.get(`/api/scenario/${scenarioId}/testrunstate`),
            "loadRunningTestRunsOfScenario",
            `loading RunningTestRuns of scenario (id:${scenarioId})`,
            callerLabel,
            callback
        ),

    loadAllScenarios: (showFromAllUsers: boolean, callerLabel: string, callback: (scenarios: Scenario[]) => void) =>
        processPromise(
            axios.get(showFromAllUsers ? "/api/scenario/all" : "/api/scenario"),
            "loadAllScenarios",
            "loading all scenarios",
            callerLabel,
            callback
        ),

    addScenario: (label: string, callerLabel: string, callback: () => void) => {
        const newScenario: NewScenario = {label};
        processPromise(
            axios.post("/api/scenario", newScenario),
            "addScenario",
            "adding a scenario",
            callerLabel,
            callback
        );
    },

    updateScenario: (scenario: Scenario, callerLabel: string, callback: (updatedScenario: Scenario) => void) =>
        processPromise(
            axios.put(`/api/scenario/${scenario.id}`, scenario),
            "updateScenario",
            "updating a scenario",
            callerLabel,
            callback
        ),

    deleteScenario: (id: string, callerLabel: string, callback: () => void) =>
        processPromise(
            axios.delete(`/api/scenario/${id}`),
            "deleteScenario",
            "deleting a scenario",
            callerLabel,
            callback
        ),

    getApiState: (callerLabel: string, callback: (apiState: ApiState) => void) =>
        processPromise(
            axios.get("/api/apistate"),
            "getApiState",
            "determining API state",
            callerLabel,
            callback
        ),

    logout: (callerLabel: string, callback: () => void) =>
        processPromise(
            axios.post("/api/logout"),
            "logout",
            "logging out",
            callerLabel,
            callback
        ),

    determineCurrentUser: (callerLabel: string, callback: (user: UserInfos) => void) =>
        processPromise(
            axios.get("/api/users/me"),
            "determineCurrentUser",
            "determining current user",
            callerLabel,
            callback
        ),

    askChatGPT: (prompt: Prompt, callerLabel: string, callback: (answer: Answer) => void) =>
        processPromise(
            axios.post("/api/ask", prompt),
            "askChatGPT",
            "sending request to ChatGPT",
            callerLabel,
            callback
        ),

};

function processPromise<T>(promise: Promise<AxiosResponse>, functionLabel: string, whenText: string, callerLabel: string, callback: (responseData: T) => void) {
    promise
        .then((response) => {
            if (response.status !== 200)
                throw new Error("Get wrong response status, when " + whenText + ": " + response.status);
            callback(response.data);
        })
        .catch((error) => {
            console.error("ERROR[" + callerLabel + "->BackendAPI." + functionLabel + "]", error);
        })
}
