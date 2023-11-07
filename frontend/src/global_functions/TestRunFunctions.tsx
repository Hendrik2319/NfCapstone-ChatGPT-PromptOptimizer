import {NewTestRun, NewTestRunDTO, TestRun, TestRunDTO} from "../models/TestRunTypes.tsx";

function convertObjectIntoMap( obj: { [ key: string ]: string[] } ): Map<string,string[]> {
    const map = new Map<string,string[]>();
    for (const key in obj) map.set(key, obj[key].map(s=>s));
    return map;
}

export function convertMapIntoObject( map: Map<string, string[]> ): { [ key: string ]: string[] } {
    const obj: { [ key: string ]: string[] } = {};
    map.forEach((value,key) => obj[key] = value.map(s=>s));
    return obj;
}


export function convertTestRunsFromDTOs( testRunDTOs: TestRunDTO[] ): TestRun[] {
    return testRunDTOs.map(convertTestRunFromDTO);
}
export function convertTestRunFromDTO( testRunDTO: TestRunDTO ): TestRun {
    return {
        id        : testRunDTO.id,
        scenarioId: testRunDTO.scenarioId,
        timestamp : new Date(testRunDTO.timestamp),
        prompt    : testRunDTO.prompt,
        variables : testRunDTO.variables.map(s=>s),
        testcases : testRunDTO.testcases.map(convertObjectIntoMap),
        answers   : testRunDTO.answers.map(value=>{ return {...value}; }),
        averageTokensPerRequest: testRunDTO.averageTokensPerRequest
    };
}


export function convertNewTestRunIntoDTO( newTestRun: NewTestRun ): NewTestRunDTO {
    return {
        //  id        : newTestRun.id,
        scenarioId: newTestRun.scenarioId,
        //  timestamp : newTestRun.timestamp,
        prompt    : newTestRun.prompt,
        variables : newTestRun.variables.map(s=>s),
        testcases : newTestRun.testcases.map(convertMapIntoObject),
        //  answers   : newTestRun.answers
        //  averageTokensPerRequest: newTestRun.averageTokensPerRequest
    };
}
export function convertNewTestRunFromDTO( newTestRun: NewTestRunDTO ): NewTestRun {
    return {
        //  id        : newTestRun.id,
        scenarioId: newTestRun.scenarioId,
        //  timestamp : newTestRun.timestamp,
        prompt    : newTestRun.prompt,
        variables : newTestRun.variables.map(s=>s),
        testcases : newTestRun.testcases.map(convertObjectIntoMap),
        //  answers   : newTestRun.answers
        //  averageTokensPerRequest: newTestRun.averageTokensPerRequest
    };
}
