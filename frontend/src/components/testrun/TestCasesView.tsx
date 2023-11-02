import {Id, Label} from "../StandardStyledComponents.tsx";
import {compareStringsIgnoringCase} from "../../Tools.tsx";
import styled from "styled-components";
import {TestCase} from "./Types.tsx";
import {SHOW_RENDERING_HINTS} from "../../Types.tsx";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

type Props = {
    testcases: TestCase[]
    getVariables?: () => string[]
    // getUsedVars: () => Set<number>
    // getVarColor: (index: number) => string
}

export default function TestCasesView( props: Readonly<Props> ) {
    const variables = props.getVariables ? props.getVariables() : null;
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestCasesView");

    function getVarNames( testcase: TestCase ) {
        if (variables != null) return variables;
        return Array
            .from(testcase.keys())
            .sort(compareStringsIgnoringCase);
    }

    function generateKey(index: number) {
        return index;
    }

    return (
        <div className="FlexRow">{
            props.testcases.map((testcase, index) => (
                <SimpleCard key={generateKey(index)}>
                    <Id>Test Case {index+1}</Id>
                    <div className="FlexColumn">{
                        getVarNames(testcase).map(varName =>
                            <SimpleCard key={varName}>
                                <Label>{varName}: </Label>
                                {testcase.get(varName)?.map(str => "\"" + str + "\"").join(", ")}
                            </SimpleCard>
                        )
                    }</div>
                </SimpleCard>
            ))
        }</div>
    )
}