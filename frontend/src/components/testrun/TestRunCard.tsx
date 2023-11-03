import {TestAnswer, TestRun} from "./Types.tsx";
import styled from "styled-components";
import {BigLabel, Id, Label} from "../StandardStyledComponents.tsx";
import TestCasesView from "./TestCasesView.tsx";
import {ChangeEvent, useState} from "react";

const BaseCard = styled.div`
  padding: 1em;
  margin: 0.5em;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: 5px 5px 5px var(--box-shadow-color);
`;

const SimpleCard = styled.div`
    border: 1px solid var(--border-color, #707070);
    border-radius: 4px;
    padding: 0.2em;
`;

const ValueBlock = styled.div`
    margin-top: 0.5em;
`;

type Props = {
    testRun: TestRun
}
type SelectedAnswerValueToShow = "answer" | "tokens"

export default function TestRunCard( props:Readonly<Props> ) {
    const [answerValueToShow, setAnswerValueToShow] = useState<SelectedAnswerValueToShow>( "answer" );

    function onChangeAnswerValueToShow( event: ChangeEvent<HTMLSelectElement> ) {
        switch (event.target.value) {
            case "answer": setAnswerValueToShow( "answer" ); break;
            case "tokens": setAnswerValueToShow( "tokens" ); break;
        }
    }

    function hasAtLeastOneTokenValue(answer: TestAnswer) {
        return (
            typeof answer.promptTokens     == "number" ||
            typeof answer.completionTokens == "number" ||
            typeof answer.totalTokens      == "number"
        );
    }

    return (
        <BaseCard>
            <Id>id         : {props.testRun.id        }</Id>
            <Id>scenarioId : {props.testRun.scenarioId}</Id>

            <ValueBlock>
                <BigLabel>timestamp : </BigLabel>
                <SimpleCard>{props.testRun.timestamp }</SimpleCard>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>prompt    : </BigLabel>
                <SimpleCard>{props.testRun.prompt    }</SimpleCard>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>variables : </BigLabel>
                <div className="FlexRow">{
                    props.testRun.variables.map(varName=> <SimpleCard key={varName}>{varName}</SimpleCard>)
                }</div>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>testcases : </BigLabel>
                <TestCasesView testcases={props.testRun.testcases}/>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>answers   : </BigLabel>
                <select value={answerValueToShow} onChange={onChangeAnswerValueToShow}>
                    <option value={"answer"}>Answers</option>
                    <option value={"tokens"}>Used ChatGPT Tokens</option>
                </select>
                <div className="FlexRow">{
                    props.testRun.answers.map(answer =>
                        <SimpleCard key={answer.indexOfTestCase+answer.label}>
                            <Id>[{answer.indexOfTestCase+1}] {answer.label}</Id>
                            {
                                answerValueToShow === "answer" && answer.answer
                            }
                            {
                                answerValueToShow === "tokens" && (
                                    hasAtLeastOneTokenValue(answer)
                                        ? <table>
                                            <tbody>
                                            <tr><td><Label>prompt     : </Label></td><td>{answer.promptTokens    } tokens</td></tr>
                                            <tr><td><Label>completion : </Label></td><td>{answer.completionTokens} tokens</td></tr>
                                            <tr><td><Label>total      : </Label></td><td>{answer.totalTokens     } tokens</td></tr>
                                            </tbody>
                                        </table>
                                        : <>No Token values</>
                                )
                            }
                        </SimpleCard>
                    )
                }</div>
            </ValueBlock>

        </BaseCard>
    )
}
