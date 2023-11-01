import {convertNewTestRunFromDTO, convertNewTestRunIntoDTO, NewTestRun, TestCase, TestRun} from "./Types.tsx";
import {FormEvent} from "react";
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

function deepcopy(oldMap: TestCase): TestCase {
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
    if (DEBUG) console.debug(`Rendering NewTestRunPanel { scenarioId: [${props.scenarioId}] }`);
    let usedVars = new Set<number>();
    let variablesCompGetter: null | (()=>string[]) = null;
    let    promptCompGetter: null | (()=>string) = null;
    let testcasesCompGetter: null | (()=>TestCase[]) = null;

    const storedNewTestRun = loadCurrentNewTestRun(props.scenarioId) ?? copyValues(props.scenarioId, props.previous);

    function getVariables(): string[]   { return !variablesCompGetter ? storedNewTestRun.variables : variablesCompGetter(); }
    function getPrompt   (): string     { return !   promptCompGetter ? storedNewTestRun.prompt    :    promptCompGetter(); }
    function getTestcases(): TestCase[] { return !testcasesCompGetter ? storedNewTestRun.testcases : testcasesCompGetter(); }

    function saveFormValues(prompt: string, variables: string[], testcases: TestCase[]) {
        saveCurrentNewTestRun(props.scenarioId, {
            prompt,
            scenarioId: props.scenarioId,
            variables,
            testcases
        })
    }

    function performTestRun() {
        const data = convertNewTestRunIntoDTO( {
            prompt: getPrompt(),
            scenarioId: props.scenarioId,
            variables: getVariables(),
            testcases: getTestcases()
        } );
        axios.post(`/api/testrun`, data)
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

    function resetForm() {
        clearCurrentNewTestRun(props.scenarioId);
    }

    function getVarColor(index: number): string {
        return "var(--text-background-var"+(index%6)+")";
    }

    // TODO: propagate changes in variables to PromptEditAndView and TestCasesEditAndView
    return (
        <>
            <Label>Prompt :</Label>
            <PromptEditAndView
                prompt={storedNewTestRun.prompt}
                getVariables={getVariables}
                getVarColor={getVarColor}
                updateUsedVars={usedVars_ => usedVars = usedVars_}
                saveFormValues={prompt => saveFormValues(prompt, getVariables(), getTestcases())}
                setGetter={fcn => promptCompGetter = fcn}
            />
            <Label>Variables :</Label>
            <VariablesEdit
                variables={storedNewTestRun.variables}
                getVarColor={getVarColor}
                saveFormValues={variables => saveFormValues(getPrompt(), variables, getTestcases())}
                setGetter={fcn => variablesCompGetter = fcn}
            />
            <Label>Test Cases :</Label>
            <TestCasesEditAndView
                testcases={storedNewTestRun.testcases}
                getVariables={getVariables}
                getUsedVars={() => usedVars}
                getVarColor={getVarColor}
                saveFormValues={testcases => saveFormValues(getPrompt(), getVariables(), testcases)}
                setGetter={fcn => testcasesCompGetter = fcn}
            />
            <Form onSubmit={onSubmitForm}>
                <BigButton type={"button"} onClick={resetForm}>Reset</BigButton>
                <BigButton>Start Test Run</BigButton>
            </Form>
        </>
    )
}
