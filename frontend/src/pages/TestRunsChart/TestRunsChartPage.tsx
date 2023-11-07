import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import {useParams} from "react-router-dom";
import {SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";
import styled from "styled-components";
import TestRunsChart from "./components/TestRunsChart.tsx";
import {useEffect, useState} from "react";
import {TestRun} from "../../models/TestRunTypes.tsx";
import {loadScenarioById, loadTestRunsOfScenario} from "../../global_functions/BackendAPI.tsx";
import {getWordCount} from "../../global_functions/Tools.tsx";
import {Scenario} from "../../models/ScenarioTypes.tsx";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

type ChartEntry = {
    label: string
    averageTokensPerRequest?: number
    amountOfAnswersMeetMaxWordCount?: number
}

export default function TestRunsChartPage() {
    const [ scenario, setScenario ] = useState<Scenario>();
    const [ testruns, setTestruns ] = useState<TestRun[]>([]);
    const { id: scenarioId } = useParams();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestRunsChartPage", { scenarioId });

    useEffect(()=>{
        if (scenarioId) {
            loadScenarioById(scenarioId, "TestRunsChartPage.useEffect", scenario=> {
                loadTestRunsOfScenario(scenarioId, "TestRunsChartPage.useEffect", testruns => {
                    setScenario(scenario);
                    setTestruns(testruns);
                });
            });
        }
    }, [ scenarioId ]);

    if (!scenarioId)
        return <>No scenario choosen.</>
    if (!scenario)
        return <>No scenario found.</>

    const entries = testruns
        .map( (testrun, index) => {
            const entry: ChartEntry = {
                label: `[${index}] ${testrun.timestamp}`,
                averageTokensPerRequest: testrun.averageTokensPerRequest,
                amountOfAnswersMeetMaxWordCount: testrun.answers.length === 0 || !scenario.maxWantedWordCount
                    ? undefined
                    : testrun.answers
                        .filter( answer => getWordCount(answer.answer) <= scenario.maxWantedWordCount!)
                        .length / testrun.answers.length
            }
            return entry;
        } )
        .filter( entry => {
            return (
                typeof entry.averageTokensPerRequest === "number" ||
                typeof entry.amountOfAnswersMeetMaxWordCount === "number"
            )
        } );

    if (entries.length === 0)
        return <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"Chart"}/>
            <SimpleCard>No values to show.</SimpleCard>
        </>

    function getValue( defaultValue: number, value?: number ) {
        if (typeof value === "number")
            return value;
        return defaultValue
    }

    const labels = entries.map( entry => entry.label );

    const averageTokensPerRequest =
        entries.map( entry => getValue( 0, entry.averageTokensPerRequest ) );

    const amountOfAnswersMeetMaxWordCount = !scenario.maxWantedWordCount ? [] :
        entries.map( entry => getValue( 0, entry.amountOfAnswersMeetMaxWordCount )*100 );

    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"Chart"}/>
            <SimpleCard>
                <TestRunsChart
                    chartTitle={"Values of Answers in TestRun"}

                    xData={labels}
                    axisXLabel={"TestRuns"}

                    lineSet={{
                        data: averageTokensPerRequest,
                        label: "Average Tokens per Request",
                        axisLabel: "Tokens per Request",
                    }}

                    barSet={
                        scenario.maxWantedWordCount
                            ? {
                                data: amountOfAnswersMeetMaxWordCount,
                                label: "Answer meets Max. Word Count",
                                axisLabel: "Amount of Answers, that meet Max. Word Count (%)",
                            }
                            : undefined
                    }
                />
            </SimpleCard>
        </>
    )
}