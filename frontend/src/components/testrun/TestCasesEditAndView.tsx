import {useEffect, useState} from "react";
import TestCasesEdit from "./TestCasesEdit.tsx";
import TestCasesView from "./TestCasesView.tsx";
import {TestCase} from "./Types.tsx";

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
    getUsedVars: () => Set<number> // TODO: delete if not needed
    getVarColor: (index: number) => string
    saveFormValues:  (testcases: TestCase[]) => void // TODO: delete if not needed
    setGetter: ( getter: ()=>TestCase[] ) => void
}

export default function TestCasesEditAndView(props:Readonly<Props> ) {
    const cleanedTestcases = cleanTestcases(props.testcases, props.getVariables());

    const [testcases, setTestcases] = useState<TestCase[]>( cleanedTestcases );
    const [mode, setMode] = useState<Mode>("view");
    props.setGetter( ()=>testcases );

    useEffect(() => {
        setTestcases(cleanedTestcases)
    }, [cleanedTestcases]);

    if (mode === "view")
        return (
            <>
                <button type={"button"} onClick={ () => setMode("edit") }>Switch to Edit</button>
                <TestCasesView testcases={testcases} getVariables={props.getVariables}/>
            </>
        );

    if (mode === "edit")
        return (
            <>
                <button type={"button"} onClick={ () => setMode("view") }>Switch to View</button>
                <TestCasesEdit testcases={testcases} getVariables={props.getVariables} getVarColor={props.getVarColor}/>
            </>
        );
}