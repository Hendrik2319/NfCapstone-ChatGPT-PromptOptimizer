export type Scenario = {
    id       : string
    authorID : string
    label    : string
    maxWantedWordCount?: number
}

export type NewScenario = {
    label    : string
}

export type ScenarioDialogOptions = {
    scenario: Scenario
}
