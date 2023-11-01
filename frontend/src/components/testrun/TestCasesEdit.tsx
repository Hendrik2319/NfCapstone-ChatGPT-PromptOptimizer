import {Id} from "../StandardStyledComponents.tsx";
import StringListInput from "./StringListInput.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {TestCase} from "./Types.tsx";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

const ColoredVarName = styled.label<{ $bgcolor: string }>`
  display: inline-block;
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.1em 0.5em;
  background: ${props => props.$bgcolor};
`;

type Props = {
    testcases: TestCase[]
    // setTestcases: (testcases: Map<string, string[]>[]) => void
    getVariables: () => string[]
    // getUsedVars: () => Set<number>
    getVarColor: (index: number) => string
}

export default function TestCasesEdit( props: Readonly<Props> ) {
    const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState<number>(0);

    const variables = props.getVariables();
    /*
        const testcases = props.testcases.map( testcase => {
            const cleanedTestcase = new Map<string, string[]>();
            variables.forEach( varName => {
                const values = testcase.get(varName);
                cleanedTestcase.set( varName, !values ? [] : values.map(s=>s) );
            } );
            return cleanedTestcase;
        } );
    */

    useEffect(() => {
        if (props.testcases.length <= selectedTestCaseIndex && 0 < props.testcases.length)
            setSelectedTestCaseIndex( props.testcases.length-1 );
    }, [selectedTestCaseIndex, props.testcases.length]);

    function onTestCaseSelectChange( event: ChangeEvent<HTMLSelectElement> ) {
        switchTo(parseInt(event.target.value));
    }

    function switchTo(newIndex: number) {
        if (newIndex<0) return;
        if (newIndex>=props.testcases.length) return;
        setSelectedTestCaseIndex(newIndex);
    }

    const selectedTestcase: TestCase | null =
        selectedTestCaseIndex < props.testcases.length
            ? props.testcases[selectedTestCaseIndex]
            : null;

    const disabled = props.testcases.length === 0;
    return (
        <div>
            {"Test Case: "}
            <select value={selectedTestCaseIndex} onChange={onTestCaseSelectChange} disabled={disabled}>
                {
                    props.testcases.map( (tc, index) =>
                        <option key={index} value={index}>{index+1}</option>
                    )
                }
            </select>
            <button type={"button"} onClick={()=>switchTo(selectedTestCaseIndex-1)} disabled={disabled}>&lt;</button>
            <button type={"button"} onClick={()=>switchTo(selectedTestCaseIndex+1)} disabled={disabled}>&gt;</button>
            {"  "}
            <button type={"button"} onClick={()=>{ /*   TODO   */ }}>Add</button>
            <button type={"button"} onClick={()=>{ /*   TODO   */ }}>Remove</button>
            {
                selectedTestcase &&
                <SimpleCard>
                    <Id>Test Case {selectedTestCaseIndex+1}</Id>
                    <div className="FlexColumn">{
                        variables.map( (varName, index) => {
                            const values = selectedTestcase.get(varName) ?? [];
                            return (
                                <SimpleCard key={varName}>
                                    <StringListInput
                                        labelComp={<ColoredVarName $bgcolor={props.getVarColor(index)}>{varName}: </ColoredVarName>}
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
        </div>
    )
}