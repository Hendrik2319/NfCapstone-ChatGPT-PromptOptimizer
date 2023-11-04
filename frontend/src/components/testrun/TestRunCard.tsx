import {TestAnswer, TestRun} from "./Types.tsx";
import styled from "styled-components";
import {BigLabel, Id, Label} from "../StandardStyledComponents.tsx";
import TestCasesView from "./newtestrun/TestCasesView.tsx";
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

const AnswerCard = styled.div<{ $bgcolor: string }>`
  background: ${props => props.$bgcolor};
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
`;

const ValueBlock = styled.div`
    margin-top: 0.5em;
`;

const StartNewButton = styled.button`
  margin-bottom: 0.5em;
`;

type Props = {
    testRun: TestRun
    rateAnswers_MaxWordCount?: number
    startNewTestRun?: ()=>void
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

    function getWordCount(answer: string) {
        const words = answer.split(/\s+/);
        return words.length;
    }

    function getBgColorOfAnswer(answer: TestAnswer) {
        if (props.rateAnswers_MaxWordCount)
            return getWordCount(answer.answer) > props.rateAnswers_MaxWordCount
                ? "var(--text-background-fail)"
                : "var(--text-background-success)";
        return "var(--background-color)";
    }

    return (
        <BaseCard>
            {
                props.startNewTestRun &&
                <StartNewButton onClick={props.startNewTestRun}>Start a new based on this</StartNewButton>
            }
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
                        <AnswerCard key={answer.indexOfTestCase+answer.label} $bgcolor={getBgColorOfAnswer(answer)}>
                            <Id>
                                {answer.indexOfTestCase>=0 && "["+(answer.indexOfTestCase+1)+"] " }
                                {answer.label}
                            </Id>
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
                        </AnswerCard>
                    )
                }</div>
                {
                    props.rateAnswers_MaxWordCount &&
                    <SimpleCard>
                        {
                            props.testRun.answers
                                .filter( answer => props.rateAnswers_MaxWordCount && getWordCount(answer.answer) <= props.rateAnswers_MaxWordCount)
                                .length
                            +" / "+
                            props.testRun.answers.length
                            +" have max. "+
                            props.rateAnswers_MaxWordCount
                            +" word(s)"
                        }
                    </SimpleCard>
                }
            </ValueBlock>

        </BaseCard>
    )
}
