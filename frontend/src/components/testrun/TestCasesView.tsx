import {Id, Label} from "../StandardStyledComponents.tsx";
import {compareStringsIgnoringCase} from "../../Tools.tsx";
import styled from "styled-components";

type Props = {
    testcases: Map<string, string[]>[]
    // setTestcases: (testcases: Map<string, string[]>[]) => void
    // getVariables: () => string[]
    // getUsedVars: () => Set<number>
    // getVarColor: (index: number) => string
    // setUpdateCallback: ( callback: ()=>void ) =>void
}

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

export default function TestCasesView( props: Readonly<Props> ) {

    return (
        <div className="FlexRow">{
            props.testcases.map((testcase, index) => (
                <SimpleCard key={index}>
                    <Id>[{index}]</Id>
                    <div className="FlexColumn">{
                        Array
                            .from(testcase.keys())
                            .sort(compareStringsIgnoringCase)
                            .map(varName =>
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