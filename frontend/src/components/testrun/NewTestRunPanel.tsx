import {convertNewTestRunFromDTO, convertNewTestRunIntoDTO, NewTestRun, TestRun} from "./Types.tsx";
import {FormEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {DEBUG} from "../../Types.tsx";
import axios from "axios";
import PromptEditAndView from "./PromptEditAndView.tsx";
import TestCasesEditAndView from "./TestCasesEditAndView.tsx";
import VariablesEdit from "./VariablesEdit.tsx";

const Form = styled.form`
  display: block;
`;

const Label = styled.label`
  margin-top: 0.5em;
  display: block;
`;

const BigButton = styled.button`
  font-size: 1.2em;
  font-weight: bold;
  padding: 0.5em 2em;
`;

function deepcopy(oldMap: Map<string, string[]>): Map<string, string[]> {
    const newMap = new Map<string, string[]>();
    oldMap.forEach(
        (value, key) => newMap.set(key, value.map(t => t)))
    return newMap;
}

function copyValues( scenarioId: string, data?: NewTestRun ) {
    if (data)
        return {
            prompt: data.prompt,
            scenarioId: scenarioId,
            variables: data.variables.map(str=>str),
            testcases: data.testcases.map(deepcopy)
        };
    return {
        prompt: "",
        scenarioId: scenarioId,
        variables: [],
        testcases: []
    };
}

const KEY_CURRENT_NEW_TEST_RUN: string = "NewTestRunPanel.CurrentNewTestRun";

function saveCurrentNewTestRun( scenarioId: string, newTestRun: NewTestRun ) {
    localStorage.setItem(KEY_CURRENT_NEW_TEST_RUN+"."+scenarioId, JSON.stringify( convertNewTestRunIntoDTO(newTestRun) ));
}
function loadCurrentNewTestRun(scenarioId: string): NewTestRun | undefined  {
    const str = localStorage.getItem(KEY_CURRENT_NEW_TEST_RUN+"."+scenarioId);
    if (str) return convertNewTestRunFromDTO( JSON.parse(str) );
}
function clearCurrentNewTestRun(scenarioId: string) {
    localStorage.setItem(KEY_CURRENT_NEW_TEST_RUN+"."+scenarioId, "");
}

type Props = {
    previous?: TestRun
    scenarioId: string
    onSuccessfulTestRun: ()=>void
}

export default function NewTestRunPanel( props:Readonly<Props> ) {
    const [prompt, setPrompt] = useState<string>("");
    const [testcases, setTestcases] = useState<Map<string, string[]>[]>([]);
    if (DEBUG) console.debug(`Rendering NewTestRunPanel { scenarioId: [${props.scenarioId}] }`);
    let usedVars = new Set<number>();
    let variablesCompGetter: null | (()=>string[]) = null;

    useEffect(() => {
        const storedNewTestRun = loadCurrentNewTestRun(props.scenarioId);
        if (storedNewTestRun) setNewTestRun(storedNewTestRun);
        else setNewTestRun( copyValues(props.scenarioId, props.previous) );
    }, [props.previous, props.scenarioId]);

    function setNewTestRun(newTestRun: NewTestRun) {
        setPrompt   (newTestRun.prompt   );
        setVariables(newTestRun.variables);
        setTestcases(newTestRun.testcases);
    }
    function getNewTestRun(): NewTestRun {
        return {
            prompt,
            scenarioId: props.scenarioId,
            variables,
            testcases
        }
    }

    function resetForm() {
        clearCurrentNewTestRun(props.scenarioId);
        setNewTestRun( copyValues(props.scenarioId, props.previous) );
    }

    function saveFormValues(prompt: string, variables: string[], testcases: Map<string, string[]>[]) {
        saveCurrentNewTestRun(props.scenarioId, {
            prompt,
            scenarioId: props.scenarioId,
            variables,
            testcases
        })
    }

    function performTestRun() {
        axios.post(`/api/testrun`, convertNewTestRunIntoDTO( getNewTestRun() ))
            .then((response) => {
                if (response.status !== 200)
                    throw new Error(`Get wrong response status, when performing a test run: ${response.status}`);
                clearCurrentNewTestRun(props.scenarioId);
                props.onSuccessfulTestRun();
            })
            .catch((error) => {
                console.error("ERROR[NewTestRunPanel.performTestRun]", error);
            })
    }

    function onSubmitForm( event: FormEvent<HTMLFormElement> ) {
        event.preventDefault();
        performTestRun();
    }

    function setChangedTestcases(testcases: Map<string, string[]>[]) {
        // TODO
    }

    function setChangedPrompt(newPrompt: string) {
        saveFormValues(newPrompt, variables, testcases);
        setPrompt(newPrompt);
    }

    function getVarColor(index: number): string {
        return "var(--text-background-var"+(index%6)+")";
    }

    function getVariables() {
        return !variablesCompGetter ? [] : variablesCompGetter();
    }

    return (
        <>
            <Label>Prompt :</Label>
            <PromptEditAndView
                prompt={prompt}
                setPrompt={setChangedPrompt}
                getVariables={getVariables}
                getVarColor={getVarColor}
                updateUsedVars={usedVars_ => usedVars = usedVars_}
            />
            <Label>Variables :</Label>
            <VariablesEdit
                variables={[]}
                getVarColor={getVarColor}
                saveFormValues={()=>{}}
                setGetter={fcn => variablesCompGetter = fcn}
            />
            <Label>Test Cases :</Label>
            <TestCasesEditAndView
                testcases={testcases.map(deepcopy)}
                setTestcases={setChangedTestcases}
                getVariables={getVariables}
                getUsedVars={() => usedVars}
                getVarColor={getVarColor}
            />
            <Form onSubmit={onSubmitForm}>
                <BigButton type={"button"} onClick={resetForm}>Reset</BigButton>
                <BigButton>Start Test Run</BigButton>
            </Form>
        </>
    )
}
