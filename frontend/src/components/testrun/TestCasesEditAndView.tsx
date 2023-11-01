import {useEffect, useState} from "react";
import TestCasesEdit from "./TestCasesEdit.tsx";
import TestCasesView from "./TestCasesView.tsx";
import {TestCase} from "./Types.tsx";

type Mode = "edit" | "view";
type Props = {
    testcases: TestCase[]
    getVariables: () => string[]
    getUsedVars: () => Set<number> // TODO
    getVarColor: (index: number) => string
    saveFormValues:  (testcases: TestCase[]) => void // TODO
    setGetter: ( getter: ()=>TestCase[] ) => void
}

export default function TestCasesEditAndView(props:Readonly<Props> ) {
    const [testcases, setTestcases] = useState<TestCase[]>(props.testcases);
    const [mode, setMode] = useState<Mode>("view");
    props.setGetter( ()=>testcases );

    useEffect(() => {
        setTestcases(props.testcases)
    }, [props.testcases]);

    function switchToEditMode() {
        setMode("edit");
    }

    function finishEditMode() {
        setMode("view");
    }

    if (mode === "view")
        return (
            <>
                <button type={"button"} onClick={switchToEditMode}>Switch to Edit</button>
                <TestCasesView testcases={testcases} getVariables={props.getVariables}/>
            </>
        );

    if (mode === "edit")
        return (
            <>
                <button type={"button"} onClick={finishEditMode}>Switch to View</button>
                <TestCasesEdit testcases={testcases} getVariables={props.getVariables} getVarColor={props.getVarColor}/>
            </>
        );
}