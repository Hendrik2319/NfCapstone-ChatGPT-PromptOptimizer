import {NewTestRun, TestRun} from "./Types.tsx";
import {ChangeEvent, FormEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {DEBUG} from "../../Types.tsx";

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
    switchBack: ()=>void
}

export default function NewTestRunPanel( props:Readonly<Props> ) {
    const [newTestRun, setNewTestRun] = useState<NewTestRun>();
    if (DEBUG) console.debug(`Rendering NewTestRunPanel { scenarioId: [${props.scenarioId}] }`);

    useEffect(() => {
        setNewTestRun( copyValues(props.scenarioId, props.previous) );
    }, [props.previous, props.scenarioId]);

    function onSubmitForm( event: FormEvent<HTMLFormElement> ) {
        event.preventDefault();
        // TODO
        // props.switchBack();
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
            <TextArea readOnly={true} rows={3}/>
            <Label>Test Cases :</Label>
            <TextArea readOnly={true} rows={3}/>
            <BigButton>Start Test Run</BigButton>
        </Form>
    )
}
