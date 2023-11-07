import {TestRun} from "../../../models/TestRunTypes.tsx";
import TestRunCard from "./TestRunCard.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {Scenario} from "../../../models/ScenarioTypes.tsx";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";

const MainPanel = styled.div`
    margin-top: 0.5em;
`;

const RateLabel = styled.label`
  display: inline-block;
  padding: 0.2em 0.5em;
`;

const InputField = styled.input`
  background: var(--background-color);
  border: 1px solid var(--border-color, #707070);
  border-radius: 3px;
  color: var(--text-color);
  padding: 0 0.5em;
  text-align: center;
`;

type Props = {
    testruns: TestRun[]
    scenario: Scenario
    startNewTestRun?: (base: TestRun)=>void
    saveChangedScenario: (scenario: Scenario)=>void
}

export default function TestRunsList( props:Readonly<Props> ) {
    const [maxWordCount, setMaxWordCount] = useState<number | undefined>(props.scenario.maxWantedWordCount);
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestRunsList", { maxWordCount });

    useEffect(() => {
        setMaxWordCount( props.scenario.maxWantedWordCount );
    }, [ props.scenario.maxWantedWordCount ]);

    function onChange_RateAnswers_WordCount_Active( event: ChangeEvent<HTMLInputElement> ) {
        const newValue = event.target.checked ? 1 : undefined;
        setMaxWordCount(newValue);
        props.saveChangedScenario({
            ...props.scenario,
            maxWantedWordCount: newValue
        });
    }

    function onChange_RateAnswers_WordCount_Value( event: ChangeEvent<HTMLInputElement> ) {
        let n = parseInt(event.target.value);
        if (isNaN(n)) n = 0;
        setMaxWordCount(n);
    }

    function save_WordCount_Value() {
        props.saveChangedScenario({
            ...props.scenario,
            maxWantedWordCount: maxWordCount
        });
    }

    function createStartNewTestRunCallback( testRun: TestRun ) {
        if (props.startNewTestRun) {
            const fcn = props.startNewTestRun;
            return () => fcn(testRun);
        }
    }

    if (props.testruns.length === 0)
        return <MainPanel>No Stored TestRuns</MainPanel>;

    return (
        <MainPanel>
            <div>
                {"Rate Answers : "}
                <RateLabel className="ButtonLike">
                    <input
                        type={"checkbox"}
                        checked={typeof maxWordCount === "number"}
                        onChange={onChange_RateAnswers_WordCount_Active}
                    />
                    {" max. Word Count"}
                    {
                        typeof maxWordCount === "number" &&
                        <>
                            {" : "}
                            <InputField
                                value={maxWordCount}
                                size={2}
                                onChange={onChange_RateAnswers_WordCount_Value}
                            />
                            {
                                maxWordCount !== props.scenario.maxWantedWordCount &&
                                <button onClick={save_WordCount_Value}>Save</button>
                            }
                        </>
                    }
                </RateLabel>
            </div>
            <div className="FlexRowNoWrap">{
                props.testruns.map(testRun =>
                    <TestRunCard
                        key={testRun.id}
                        testRun={testRun}
                        rateAnswers_MaxWordCount={maxWordCount}
                        startNewTestRun={createStartNewTestRunCallback(testRun)}
                    />
                )
            }</div>
        </MainPanel>
    )
}