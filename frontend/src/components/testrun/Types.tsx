export type TestRun = {
    id        : string
    scenarioId: string
    timestamp : string
    prompt    : string
    variables : string[]
    testcases : TestCase[]
    answers   : TestAnswer[]
}

export type TestCase = Map<string, string[]> // TODO: bug: data will not be converted from JSON into Map type

export type TestAnswer = {
    indexOfTestCase: number
    label : string
    answer: string
}
