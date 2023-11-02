import {useEffect, useState} from "react";
import TestCasesEdit from "./TestCasesEdit.tsx";
import TestCasesView from "./TestCasesView.tsx";
import {TestCase, VariablesChangeMethod} from "./Types.tsx";
import {SHOW_RENDERING_HINTS} from "../../Types.tsx";

type Mode = "edit" | "view";
type Props = {
    testcases: TestCase[]
    getVariables: () => string[]
    getUsedVars: () => Set<number> // delete if not needed
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

    if (mode === "view")
        return (
            <>
                <button type={"button"} onClick={ () => setMode("edit") }>Switch to Edit</button>
                <TestCasesView
                    testcases={testcases}
                    getVariables={props.getVariables}
                />
            </>
        );

    if (mode === "edit")
        return (
            <>
                <button type={"button"} onClick={ () => setMode("view") }>Switch to View</button>
                <TestCasesEdit
                    testcases={testcases}
                    setTestcases={onChangedTestcases}
                    getVariables={props.getVariables}
                    getVarColor={props.getVarColor}
                />
            </>
        );
}