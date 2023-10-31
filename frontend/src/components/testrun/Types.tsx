export type TestRun    = TestRunBase<TestCase>
export type TestRunDTO = TestRunBase<{ [ key: string ]: string[] }>
export type NewTestRun    = NewTestRunBase<TestCase>
export type NewTestRunDTO = NewTestRunBase<{ [ key: string ]: string[] }>

type TestRunBase<TestCaseType> = {
    id        : string
    scenarioId: string
    timestamp : string
    prompt    : string
    variables : string[]
    testcases : TestCaseType[]
    answers   : TestAnswer[]
}

type NewTestRunBase<TestCaseType> = {
//  id        : string  - defined by database
    scenarioId: string
//  timestamp : string  - defined by backend
    prompt    : string
    variables : string[]
    testcases : TestCaseType[]
//  answers   : TestAnswer[]  - defined by backend as results from external API
}

export type TestCase = Map<string, string[]>

export type TestAnswer = {
    indexOfTestCase: number
    label : string
    answer: string
}


function convertObjectIntoMap<V>( obj: { [ key: string ]: V } ): Map<string,V> {
    const map = new Map<string,V>();
    for (const key in obj) map.set(key, obj[key]);
    return map;
}

export function convertMapIntoObject<V>( map: Map<string, V> ): { [ key: string ]: V } {
    const obj: { [ key: string ]: V } = {};
    map.forEach((value,key) => obj[key] = value);
    return obj;
}


export function convertTestRunsFromDTOs( testRunDTOs: TestRunDTO[] ): TestRun[] {
    return testRunDTOs.map(convertTestRunFromDTO);
}
export function convertTestRunFromDTO( testRunDTO: TestRunDTO ): TestRun {
    return {
        id        : testRunDTO.id,
        scenarioId: testRunDTO.scenarioId,
        timestamp : testRunDTO.timestamp,
        prompt    : testRunDTO.prompt,
        variables : testRunDTO.variables,
        testcases : testRunDTO.testcases.map(convertObjectIntoMap),
        answers   : testRunDTO.answers
    };
}


export function convertNewTestRunIntoDTO( newTestRun: NewTestRun ): NewTestRunDTO {
    return {
    //  id        : newTestRun.id,
        scenarioId: newTestRun.scenarioId,
    //  timestamp : newTestRun.timestamp,
        prompt    : newTestRun.prompt,
        variables : newTestRun.variables,
        testcases : newTestRun.testcases.map(convertMapIntoObject),
    //  answers   : newTestRun.answers
    };
}
export function convertNewTestRunFromDTO( newTestRun: NewTestRunDTO ): NewTestRun {
    return {
    //  id        : newTestRun.id,
        scenarioId: newTestRun.scenarioId,
    //  timestamp : newTestRun.timestamp,
        prompt    : newTestRun.prompt,
        variables : newTestRun.variables,
        testcases : newTestRun.testcases.map(convertObjectIntoMap),
    //  answers   : newTestRun.answers
    };
}
