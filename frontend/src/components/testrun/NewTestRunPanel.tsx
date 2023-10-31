import {convertNewTestRunIntoDTO, NewTestRun, TestRun} from "./Types.tsx";
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

function copyValues( scenarioId: string, data?: NewTestRun ) {
    if (data)
        return {
            prompt: data.prompt,
            scenarioId: scenarioId,
            variables: data.variables,
            testcases: data.testcases
        };
    return {
        prompt: "",
        scenarioId: scenarioId,
        variables: [],
        testcases: []
    };
}

type Props = {
    previous?: TestRun
    scenarioId: string
    onSuccessfulTestRun: ()=>void
}

export default function NewTestRunPanel( props:Readonly<Props> ) {
    const [newTestRun, setNewTestRun] = useState<NewTestRun>();
    if (DEBUG) console.debug(`Rendering NewTestRunPanel { scenarioId: [${props.scenarioId}] }`);

    useEffect(() => {
        setNewTestRun( copyValues(props.scenarioId, props.previous) );
    }, [props.previous, props.scenarioId]);

    function performTestRun() {
        if (newTestRun)
            axios.post(`/api/testrun`, convertNewTestRunIntoDTO(newTestRun))
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
        const changedNewTestRun: NewTestRun = copyValues(props.scenarioId, newTestRun);
        changedNewTestRun.prompt = event.target.value;
        setNewTestRun(changedNewTestRun);
    }

    return (
        <Form onSubmit={onSubmitForm}>
            <Label htmlFor="prompt">Prompt :</Label>
            <TextArea id="prompt" value={newTestRun?.prompt} onChange={onPromptInput} rows={10}/>
            <Label>Variables :</Label>
            <SimpleCard>
                <StringListInput
                    values={[]}
                    fieldSize={10}
                    // onAddValue      ={(value, index) => console.debug("Vars.AddValue   ( value:\"" + value + "\", index:" + index + " )")}
                    // onChangeValue   ={(value, index) => console.debug("Vars.ChangeValue( value:\"" + value + "\", index:" + index + " )")}
                    // allowDeleteValue={(value, index) => { console.debug("Vars.DeleteValue( value:\"" + value + "\", index:" + index + " )"); return true; }}
                    onAddValue      ={()=>{}}
                    onChangeValue   ={()=>{}}
                    allowDeleteValue={()=>true}
                />
            </SimpleCard>
            <Label>Test Cases :</Label>
            <TextArea readOnly={true} rows={3}/>
            <BigButton>Start Test Run</BigButton>
        </Form>
    )
}
