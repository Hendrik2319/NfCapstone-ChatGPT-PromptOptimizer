import {TestAnswer, TestRun} from "../../../models/TestRunTypes.tsx";
import styled from "styled-components";
import {BigLabel, Id, Label, SimpleCard} from "../../../components/StandardStyledComponents.tsx";
import TestCasesView from "../../NewTestRun/components/TestCasesView.tsx";
import {ChangeEvent, useState} from "react";
import {getWordCount} from "../../../global_functions/Tools.tsx";

const BaseCard = styled.div`
  padding: 1em;
  margin: 0.5em;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: 5px 5px 5px var(--box-shadow-color);
`;

const SimpleCardNoBg02 = styled(SimpleCard)`
  padding: 0.2em;
  background: none;
`;
const SimpleCardNoBg05 = styled(SimpleCard)`
  padding: 0.5em;
  background: none;
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
                <StartNewButton onClick={props.startNewTestRun}>New TestRun</StartNewButton>
            }
            <Id>id         : {props.testRun.id        }</Id>
            <Id>scenarioId : {props.testRun.scenarioId}</Id>

            <ValueBlock>
                <BigLabel>Timestamp : </BigLabel>
                <SimpleCardNoBg05>{props.testRun.timestamp.toLocaleString()}</SimpleCardNoBg05>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>Prompt    : </BigLabel>
                <SimpleCardNoBg05>{props.testRun.prompt    }</SimpleCardNoBg05>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>Variables : </BigLabel>
                {
                    props.testRun.variables.length===0 &&
                    <SimpleCardNoBg05>Currently are no Variables defined.</SimpleCardNoBg05>
                }
                {
                    props.testRun.variables.length!==0 &&
                    <SimpleCardNoBg05 className="FlexRow">{
                        props.testRun.variables.map(varName=> <SimpleCardNoBg02 key={varName}>{varName}</SimpleCardNoBg02>)
                    }</SimpleCardNoBg05>
                }
            </ValueBlock>

            <ValueBlock>
                <BigLabel>TestCases : </BigLabel>
                <TestCasesView testcases={props.testRun.testcases}/>
            </ValueBlock>

            <ValueBlock>
                <BigLabel>Answers   : </BigLabel>
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
                                            <tr><td><Label>Prompt     : </Label></td><td>{answer.promptTokens    } tokens</td></tr>
                                            <tr><td><Label>Completion : </Label></td><td>{answer.completionTokens} tokens</td></tr>
                                            <tr><td><Label>Total      : </Label></td><td>{answer.totalTokens     } tokens</td></tr>
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
                    <SimpleCardNoBg02>
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
                    </SimpleCardNoBg02>
                }
                {
                    typeof props.testRun.averageTokensPerRequest==="number" &&
                    <SimpleCardNoBg02>
                        Average cost: {props.testRun.averageTokensPerRequest.toLocaleString(undefined, {maximumFractionDigits:2})} tokens per request
                    </SimpleCardNoBg02>
                }
            </ValueBlock>

        </BaseCard>
    )
}
