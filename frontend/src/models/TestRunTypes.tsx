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
    promptTokens?    : number
    completionTokens?: number
    totalTokens?     : number
}

export type VariablesChangeMethod = (index: number, oldVarName: string, newVarName: string) => void

export type RunningTestRun = {
    promptIndex: number,
    totalAmountOfPrompts: number,
    prompt: string,
    label : string
}
