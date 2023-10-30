export type TestRun    = TestRunBase<TestCase>
export type TestRunDTO = TestRunBase<{ [ key: string ]: string[] }>

type TestRunBase<TestCaseType> = {
    id        : string
    scenarioId: string
    timestamp : string
    prompt    : string
    variables : string[]
    testcases : TestCaseType[]
    answers   : TestAnswer[]
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


export function convertTestRunsIntoDTOs( testRuns: TestRun[] ): TestRunDTO[] {
    return testRuns.map(convertTestRunIntoDTO);
}
export function convertTestRunIntoDTO( testRun: TestRun ): TestRunDTO {
    return {
        id        : testRun.id,
        scenarioId: testRun.scenarioId,
        timestamp : testRun.timestamp,
        prompt    : testRun.prompt,
        variables : testRun.variables,
        testcases : testRun.testcases.map(convertMapIntoObject),
        answers   : testRun.answers
    };
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
