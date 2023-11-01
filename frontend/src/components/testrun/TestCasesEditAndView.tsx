import {useEffect, useState} from "react";
import TestCasesEdit from "./TestCasesEdit.tsx";
import TestCasesView from "./TestCasesView.tsx";
import {TestCase, VariablesChangeMethod} from "./Types.tsx";
import {SHOW_RENDERING_HINTS} from "../../Types.tsx";

function cleanTestcases(rawTestcases: TestCase[], variables: string[]) {
    return rawTestcases.map(testcase => {
        const cleanedTestcase: TestCase = new Map<string, string[]>();
        variables.forEach(varName => {
            const values = testcase.get(varName);
            cleanedTestcase.set(varName, !values ? [] : values.map(s => s));
        });
        return cleanedTestcase;
    });
}

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
    const variables = props.getVariables();
    const [testcases, setTestcases] = useState<TestCase[]>( cleanTestcases(props.testcases, variables) );
    const [mode, setMode] = useState<Mode>("view");
    const [renderTrigger, setRenderTrigger] = useState<boolean>(true);
    props.setGetter( ()=>testcases );
    props.setVarChangeNotifier( ()=>setRenderTrigger(!renderTrigger) );
    if (SHOW_RENDERING_HINTS) console.debug(`Rendering TestCasesEditAndView {}`);
    console.debug("TestCasesEditAndView", variables);

    useEffect(() => {
        setTestcases( cleanTestcases(props.testcases, variables) )
    }, [props.testcases, variables]);

    function onChangedTestcases( testcases: TestCase[] ) {
        props.onTestcasesChange(testcases);
        setTestcases(testcases);
    }

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