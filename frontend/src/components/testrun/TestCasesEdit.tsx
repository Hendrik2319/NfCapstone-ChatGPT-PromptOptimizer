import {Id, Label} from "../StandardStyledComponents.tsx";
import StringListInput from "./StringListInput.tsx";
import {ChangeEvent, useState} from "react";
import styled from "styled-components";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

type Props = {
    testcases: Map<string, string[]>[]
    // setTestcases: (testcases: Map<string, string[]>[]) => void
    getVariables: () => string[]
    // getUsedVars: () => Set<number>
    // getVarColor: (index: number) => string
    // setUpdateCallback: ( callback: ()=>void ) =>void
}

export default function TestCasesEdit( props: Readonly<Props> ) {
    const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState<number>(0);

    function onTestCaseSelectChange( event: ChangeEvent<HTMLSelectElement> ) {
        switchTo(parseInt(event.target.value));
    }

    function switchTo(newIndex: number) {
        if (newIndex<0) return;
        if (newIndex>=props.testcases.length) return;
        setSelectedTestCaseIndex(newIndex);
    }

    const selectedTestcase: Map<string, string[]> | null =
        selectedTestCaseIndex < props.testcases.length
            ? props.testcases[selectedTestCaseIndex]
            : null;

    const variables = props.getVariables();

    return (
        <>
            <button type={"button"}>Edit</button>
            {"Test Case: "}
            <select value={selectedTestCaseIndex} onChange={onTestCaseSelectChange}>
                {
                    props.testcases.map( (tc, index) =>
                        <option key={index} value={index}>{index+1}</option>
                    )
                }
            </select>
            <button type={"button"} onClick={()=>switchTo(selectedTestCaseIndex-1)}>&lt;</button>
            <button type={"button"} onClick={()=>switchTo(selectedTestCaseIndex+1)}>&gt;</button>
            <button type={"button"} onClick={()=>{ /*   TODO   */ }}>Add</button>
            {
                selectedTestcase &&
                <SimpleCard>
                    <Id>Test Case {selectedTestCaseIndex+1}</Id>
                    <div className="FlexColumn">{
                        variables.map( (varName: string) => {
                            const values = selectedTestcase.get(varName) ?? [];
                            return (
                                <SimpleCard key={varName}>
                                    <Label>{varName}: </Label>
                                    <StringListInput
                                        values={values}
                                        fieldSize={10}
                                        // getFieldBgColor={getVarColor}
                                        onAddValue      ={()=>{} /*   TODO   */}
                                        onChangeValue   ={()=>{} /*   TODO   */}
                                        allowDeleteValue={()=>true /*   TODO   */}
                                    />
                                </SimpleCard>
                            )
                        })
                    }</div>
                </SimpleCard>
            }
        </>
    )
}