import {useEffect, useState} from "react";
import TestCasesEdit from "./TestCasesEdit.tsx";
import TestCasesView from "./TestCasesView.tsx";
import {TestCase, VariablesChangeMethod} from "../../../models/TestRunTypes.tsx";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {ButtonSVG, SimpleCard} from "../../../components/StandardStyledComponents.tsx";
import {SVGsInVars} from "../../../assets/SVGsInVars.tsx";
import styled from "styled-components";

const SwitchButton = styled.button`
  height: 2em;
  padding: 0 1em;
`;

type Mode = "edit" | "view";
type Props = {
    testcases: TestCase[]
    getVariables: () => string[]
    getUsedVars: () => Set<number>
    getVarColor: (index: number) => string
    onTestcasesChange:  (testcases: TestCase[]) => void
    setGetter: ( getter: ()=>TestCase[] ) => void
    setVarChangeNotifier: ( notifier: VariablesChangeMethod )=>void
}

export default function TestCasesEditAndView(props:Readonly<Props> ) {
    const [testcases, setTestcases] = useState<TestCase[]>( props.testcases );
    const [mode, setMode] = useState<Mode>("view");
    const [renderTrigger, setRenderTrigger] = useState<boolean>(true);
    props.setGetter( ()=>testcases );
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestCasesEditAndView");

    useEffect(() => {
        setTestcases( props.testcases );
    }, [props.testcases]);

    function onChangedTestcases( testcases: TestCase[] ) {
        props.onTestcasesChange(testcases);
        setTestcases(testcases);
    }

    props.setVarChangeNotifier( (index: number, oldVarName: string, newVarName: string) => {
        if (oldVarName==="" || newVarName==="")
            setRenderTrigger(!renderTrigger);
        else {
            onChangedTestcases(
                testcases.map( (testcase) => {
                    const changedTestcase = new Map<string, string[]>();
                    testcase.forEach((values, varName) => {
                        if (oldVarName===varName) varName = newVarName;
                        changedTestcase.set(varName, Array.from(values));
                    })
                    return changedTestcase;
                })
            );
        }
    } );

    function computeNumberOfAPIRequests() {
        const usedVars = props.getUsedVars();
        const variables = props.getVariables().filter((varName, index) => usedVars.has(index))
        return testcases
            .map(
                testcase => {
                    return variables
                        .map(
                            varName => {
                                const values = testcase.get(varName);
                                return !values ? 0 : values.length;
                            }
                        )
                        .reduce( (n1,n2)=>n1*n2, 1 );
                }
            )
            .reduce( (n1,n2)=>n1+n2, 0 );
    }

    const numberOfAPIRequests = computeNumberOfAPIRequests();
    return (
        <SimpleCard>
            {
                mode === "view" &&
                <>
                    <SwitchButton onClick={ () => setMode("edit") }>
                        Edit <ButtonSVG>{ SVGsInVars.Edit }</ButtonSVG>
                    </SwitchButton>
                    <TestCasesView
                        testcases={testcases}
                        getVariables={props.getVariables}
                    />
                </>
            }
            {
                mode === "edit" &&
                <>
                    <SwitchButton onClick={ () => setMode("view") }>
                        View
                    </SwitchButton>
                    <TestCasesEdit
                        testcases={testcases}
                        setTestcases={onChangedTestcases}
                        getVariables={props.getVariables}
                        getVarColor={props.getVarColor}
                    />
                </>
            }
            {
                testcases.length > 0 &&
                <SimpleCard>Number of needed API requests: {numberOfAPIRequests} {numberOfAPIRequests>20 && "!!"}</SimpleCard>
            }
        </SimpleCard>
    );
}