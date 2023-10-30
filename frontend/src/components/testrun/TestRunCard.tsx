import {TestAnswer, TestCase, TestRun} from "./Types.tsx";
import styled from "styled-components";
import {Id, Label} from "../StandardStyledComponents.tsx";

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

    function convertVarValues(values: string[], varName: string) {
        return (
            <SimpleCard key={varName}>
                <Label>{varName}: </Label>
                {values.map( str=>"\""+str+"\"" ).join(", ")}
            </SimpleCard>
        )
    }
    function convertTestCase(testcase: TestCase, index: number) {
        function sortVarNames(str1: string, str2: string): number {
            const str1lc = str1.toLowerCase();
            const str2lc = str2.toLowerCase();
            if (str1lc < str2lc) return -1;
            if (str1lc > str2lc) return +1;
            if (str1 < str2) return -1;
            if (str1 > str2) return +1;
            return 0;
        }

        return (
            <SimpleCard key={index}>
                <Id>[{index}]</Id>
                <div className="FlexColumn">{
                    Array.from(testcase.keys()).sort(sortVarNames).map( (varName: string) => {
                            const values = testcase.get(varName);
                            if (values) return convertVarValues(values, varName);
                    })
                }</div>
            </SimpleCard>
        )
    }

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
            <div><Label>testcases : </Label><div className="FlexRow">{
                props.testRun.testcases.map(convertTestCase)
            }</div></div>
            <div><Label>answers   : </Label><div className="FlexRow">{
                props.testRun.answers  .map(convertAnswer)
            }</div></div>
        </BaseCard>
    )
}
