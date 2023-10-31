import {convertNewTestRunFromDTO, convertNewTestRunIntoDTO, NewTestRun, TestRun} from "./Types.tsx";
import {ChangeEvent, FormEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {DEBUG} from "../../Types.tsx";
import axios from "axios";
import StringListInput from "./StringListInput.tsx";

const Form = styled.form`
  display: block;
  margin-top: 0.5em;
`;

const TextArea = styled.textarea`
  width: 100%;
  box-sizing: border-box;
`;

const Label = styled.label`
  display: block;
`;

const BigButton = styled.button`
  font-size: 1.2em;
  font-weight: bold;
  padding: 0.5em 2em;
`;

const SimpleCard = styled.div`
  display: block;
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
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
    const [variables, setVariables] = useState<string[]>([]);
    const [testcases, setTestcases] = useState<Map<string, string[]>[]>([]);
    if (DEBUG) console.debug(`Rendering NewTestRunPanel { scenarioId: [${props.scenarioId}] }`);

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

    function performTestRun() {
        axios.post(`/api/testrun`, convertNewTestRunIntoDTO( getNewTestRun() ))
            .then((response) => {
                if (response.status !== 200)
                    throw new Error(`Get wrong response status, when performing a test run: ${response.status}`);
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

    function onPromptInput( event: ChangeEvent<HTMLTextAreaElement> ) {
        setPrompt(event.target.value)
    }

    function onAddVariable(value: string) {
        const changedVariables = [...variables];
        changedVariables.push(value);
        setVariables(changedVariables);
    }

    function onChangeVariable(value: string, index: number) {
        const changedVariables = [...variables];
        changedVariables[index] = value;
        setVariables(changedVariables);
    }

    function allowDeleteVariable(value: string, index: number): boolean {
        const changedVariables = [...variables];
        changedVariables.splice(index, 1);
        setVariables(changedVariables);
        return true;
    }

    function convertTestcasesToString(testcases: Map<string, string[]>[]) {
        return testcases.map(
            (map, index) => {
                return "["+ index +"] "+ Array.from(map.keys()).sort().map(
                    varName => {
                        const strings = map.get(varName);
                        if (strings)
                            return varName +": [ "+ strings.join(",") +" ]";
                        else
                            return varName +": --";
                    }
                ).join(", ");
            }
        ).join("\r\n");
    }

    return (
        <Form onSubmit={onSubmitForm}>
            <Label htmlFor="prompt">Prompt :</Label>
            <TextArea id="prompt" value={prompt} onChange={onPromptInput} rows={10}/>
            <Label>Variables :</Label>
            <SimpleCard>
                <StringListInput
                    values={variables}
                    fieldSize={10}
                    onAddValue      ={onAddVariable}
                    onChangeValue   ={onChangeVariable}
                    allowDeleteValue={allowDeleteVariable}
                />
            </SimpleCard>
            <Label>Test Cases :</Label>
            <TextArea readOnly={true} rows={3} value={convertTestcasesToString(testcases)}/>
            <BigButton type={"button"} onClick={resetForm}>Reset</BigButton>
            <BigButton>Start Test Run</BigButton>
            <BigButton type={"button"} onClick={()=>saveCurrentNewTestRun(props.scenarioId, getNewTestRun())}>Save</BigButton>
            <BigButton type={"button"} onClick={()=>console.debug("loadCurrentNewTestRun", loadCurrentNewTestRun(props.scenarioId))}>TestLoad</BigButton>
            <BigButton type={"button"} onClick={()=>clearCurrentNewTestRun(props.scenarioId)}>Clear</BigButton>
        </Form>
    )
}
