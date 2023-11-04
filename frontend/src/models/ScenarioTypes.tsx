export type Scenario = {
    id       : string
    authorID : string
    label    : string
}

export type NewScenario = {
    label    : string
}

export type ScenarioDialogOptions = {
    scenario: Scenario
}
