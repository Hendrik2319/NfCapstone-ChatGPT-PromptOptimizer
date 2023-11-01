import {useState} from "react";
import TestCasesEdit from "./TestCasesEdit.tsx";
import TestCasesView from "./TestCasesView.tsx";

type Mode = "edit" | "view";
type Props = {
    testcases: Map<string, string[]>[]
    setTestcases: (testcases: Map<string, string[]>[]) => void
    getVariables: () => string[]
    getUsedVars: () => Set<number>
    getVarColor: (index: number) => string
    setUpdateCallback: ( callback: ()=>void ) =>void
}

export default function TestCasesEditAndView(props:Readonly<Props> ) {
    const [mode, setMode] = useState<Mode>("view");

    if (mode === "view")
        return (
            <>
                <button type={"button"}>Edit</button>
                <TestCasesView testcases={props.testcases}/>
            </>
        );

    if (mode === "edit")
        return (
            <>
                <button type={"button"}>Set</button>
                <TestCasesEdit testcases={props.testcases} getVariables={props.getVariables}/>
            </>
        );
}