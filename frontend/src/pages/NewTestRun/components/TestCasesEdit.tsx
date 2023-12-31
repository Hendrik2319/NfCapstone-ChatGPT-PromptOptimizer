import {ButtonSVG, Id, SimpleCard} from "../../../components/StandardStyledComponents.tsx";
import StringListInput from "./StringListInput.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {TestCase} from "../../../models/TestRunTypes.tsx";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {SVGsInVars} from "../../../assets/SVGsInVars.tsx";

const ColoredVarName = styled.label<{ $bgcolor: string }>`
  display: inline-block;
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em 0.5em;
  margin-bottom: 0.2em;
  background: ${props => props.$bgcolor};
`;

const TestCasesEditOptions = styled.div`
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  margin-bottom: 0.5em;

  .Spacer {
    width: 1em;
  }
  
  button, select {
    height: 2em;
  }
`;

const TestCasesList = styled(SimpleCard)`
  padding: 0.5em;
`;

function deepcopy(oldMap: TestCase): TestCase {
    const newMap = new Map<string, string[]>();
    oldMap.forEach(
        (value, key) => newMap.set(key, value.map(t => t)))
    return newMap;
}

type Props = {
    testcases: TestCase[]
    setTestcases: (testcases: TestCase[]) => void
    getVariables: () => string[]
    // getUsedVars: () => Set<number>
    getVarColor: (index: number) => string
}

export default function TestCasesEdit( props: Readonly<Props> ) {
    const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState<number>(0);
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestCasesEdit");

    const selectedTestcase: TestCase | null =
        selectedTestCaseIndex < props.testcases.length
            ? props.testcases[selectedTestCaseIndex]
            : null;

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


    function changeVariable(varName: string, changeAction: (changedVariables: string[]) => void ) {
        if (!selectedTestcase) return;
        const values = Array.from(selectedTestcase.get(varName) ?? []);
        changeAction(values);
        const copy = props.testcases.map(deepcopy);
        copy[selectedTestCaseIndex].set(varName, values);
        props.setTestcases(copy);
    }
    function allowAddValue(varName: string, value: string) {
        changeVariable(varName, values => values.push(value));
        return true;
    }
    function allowChangeValue(varName: string, value: string, index: number) {
        changeVariable(varName, values => values[index] = value);
        return true;
    }
    function allowDeleteValue(varName: string, value: string, index: number) {
        changeVariable(varName, values => values.splice(index, 1));
        return true;
    }


    function addTestCase() {
        const copy = props.testcases.map(deepcopy);
        copy.push(new Map<string, string[]>());
        setSelectedTestCaseIndex(copy.length-1);
        props.setTestcases(copy);
    }
    function removeTestCase() {
        if (0 < props.testcases.length) {
            const copy = props.testcases.map(deepcopy);
            props.setTestcases(copy.slice(0,-1));
        }
    }


    function generateKey(index: number) {
        return index;
    }

    return (
        <TestCasesList>
            <TestCasesEditOptions>
                {
                    props.testcases.length > 0 &&
                    <>
                        <button onClick={()=>switchTo(selectedTestCaseIndex-1)}>&lt;</button>
                        <select value={selectedTestCaseIndex} onChange={onTestCaseSelectChange}>
                            {
                                props.testcases.map( (tc, index) =>
                                    <option key={generateKey(index)} value={index}>Test Case {index+1}</option>
                                )
                            }
                        </select>
                        <button type={"button"} onClick={()=>switchTo(selectedTestCaseIndex+1)}>&gt;</button>
                    </>
                }
                <span className={"Spacer"}></span>
                <div>
                    <button onClick={addTestCase   }>Add <ButtonSVG>{ SVGsInVars.AddDoc }</ButtonSVG></button>
                    <button onClick={removeTestCase}>Remove <ButtonSVG>{ SVGsInVars.RemoveDoc }</ButtonSVG></button>
                </div>
            </TestCasesEditOptions>
            {
                selectedTestcase &&
                <SimpleCard>
                    <Id>Test Case {selectedTestCaseIndex+1}</Id>
                    <div className="FlexColumn">{
                        props.getVariables().map( (varName, index) => {
                            const values = selectedTestcase.get(varName) ?? [];
                            return (
                                <SimpleCard key={varName}>
                                    <StringListInput
                                        labelComp={<ColoredVarName $bgcolor={props.getVarColor(index)}>{varName}: </ColoredVarName>}
                                        values={values}
                                        fieldSize={10}
                                        // getFieldBgColor={getVarColor}
                                        allowAddValue   ={(value: string               ) => allowAddValue   (varName, value       )}
                                        allowChangeValue={(value: string, index: number) => allowChangeValue(varName, value, index)}
                                        allowDeleteValue={(value: string, index: number) => allowDeleteValue(varName, value, index)}
                                    />
                                </SimpleCard>
                            )
                        })
                    }</div>
                </SimpleCard>
            }
        </TestCasesList>
    )
}