import {Scenario} from "../models/ScenarioTypes.tsx";
import {convertNewTestRunIntoDTO, convertTestRunsFromDTOs, NewTestRun, TestRun} from "../models/TestRunTypes.tsx";
import axios from "axios";

export function loadScenario( scenarioId: string, callerLabel: string, callback: (scenario: Scenario)=>void ){
    axios.get(`/api/scenario/${scenarioId}`)
        .then((response) => {
            if (response.status!==200)
                throw new Error(`Get wrong response status, when loading scenario (id:${scenarioId}): ${response.status}`);
            callback(response.data)
        })
        .catch((error)=>{
            console.error("ERROR["+callerLabel+"->BackendAPI.loadScenario]", error);
        })
}

export function loadTestRuns( scenarioId: string, callerLabel: string, callback: (testruns: TestRun[])=>void ){
    axios.get(`/api/scenario/${scenarioId}/testrun`)
        .then((response) => {
            if (response.status!==200)
                throw new Error(`Get wrong response status, when loading test runs: ${response.status}`);
            callback(convertTestRunsFromDTOs(response.data))
        })
        .catch((error)=>{
            console.error("ERROR["+callerLabel+"->BackendAPI.loadTestRuns]", error);
        })
}

export function performTestRun( newTestRun: NewTestRun, callerLabel: string, onSuccess: ()=>void ){
    const data = convertNewTestRunIntoDTO( newTestRun );
    axios.post(`/api/testrun`, data)
        .then((response) => {
            if (response.status !== 200)
                throw new Error(`Get wrong response status, when performing a test run: ${response.status}`);
            onSuccess();
        })
        .catch((error) => {
            console.error("ERROR["+callerLabel+"->BackendAPI.performTestRun]", error);
        })
}
