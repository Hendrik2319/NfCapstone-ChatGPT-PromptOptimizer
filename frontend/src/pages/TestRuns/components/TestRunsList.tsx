import {TestRun} from "../../../models/TestRunTypes.tsx";
import TestRunCard from "./TestRunCard.tsx";
import {ChangeEvent, useState} from "react";
import styled from "styled-components";

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
    scenarioId: string
    startNewTestRun?: (base: TestRun)=>void
}

export default function TestRunsList( props:Readonly<Props> ) {
    const [maxWordCount, setMaxWordCount] = useState<number>();

    function onChange_RateAnswers_WordCount_Active( event: ChangeEvent<HTMLInputElement> ) {
        if (event.target.checked)
            setMaxWordCount(1);
        else
            setMaxWordCount(undefined);
    }

    function onChange_RateAnswers_WordCount_Value( event: ChangeEvent<HTMLInputElement> ) {
        let n = parseInt(event.target.value);
        if (isNaN(n)) n = 0;
        setMaxWordCount(n);
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
                        checked={maxWordCount!==undefined}
                        onChange={onChange_RateAnswers_WordCount_Active}
                    />
                    {" max. Word Count"}
                    {
                        maxWordCount!==undefined &&
                        <>
                            {" : "}
                            <InputField
                                value={maxWordCount}
                                size={2}
                                onChange={onChange_RateAnswers_WordCount_Value}
                            />
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