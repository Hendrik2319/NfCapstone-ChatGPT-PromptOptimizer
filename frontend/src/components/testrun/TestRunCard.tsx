import {TestAnswer, TestRun} from "./Types.tsx";
import styled from "styled-components";
import {Id, Label} from "../StandardStyledComponents.tsx";
import TestCasesView from "./TestCasesView.tsx";

type Props = {
    testRun: TestRun
}

const SimpleCard = styled.div`
    display: block;
    border: 1px solid var(--border-color, #707070);
    border-radius: 4px;
    padding: 0.2em;
`;

const BaseCard = styled.div`
  padding: 1em;
  margin: 0.5em;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: 5px 5px 5px var(--box-shadow-color);
`;

export default function TestRunCard( props:Readonly<Props> ) {

    function convertAnswer(answer: TestAnswer) {
        return (
            <SimpleCard key={answer.indexOfTestCase+answer.label}>
                <div>[{answer.indexOfTestCase}] {answer.label}</div>
                <div><Label>answer : </Label>{answer.answer}</div>
            </SimpleCard>
        )
    }

    return (
        <BaseCard>
            <Id>id         : {props.testRun.id        }</Id>
            <Id>scenarioId : {props.testRun.scenarioId}</Id>
            <div><Label>timestamp : </Label><SimpleCard>{props.testRun.timestamp }</SimpleCard></div>
            <div><Label>prompt    : </Label><SimpleCard>{props.testRun.prompt    }</SimpleCard></div>
            <div><Label>variables : </Label><div className="FlexRow">{
                props.testRun.variables.map(varName=> <SimpleCard key={varName}>{varName}</SimpleCard>)
            }</div></div>
            <div><Label>testcases : </Label><TestCasesView testcases={props.testRun.testcases}/></div>
            <div><Label>answers   : </Label><div className="FlexRow">{
                props.testRun.answers.map(convertAnswer)
            }</div></div>
        </BaseCard>
    )
}
