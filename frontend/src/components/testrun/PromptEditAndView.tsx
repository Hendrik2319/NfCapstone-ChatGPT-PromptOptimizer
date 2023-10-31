import {ChangeEvent, useEffect, useState} from "react";
import styled from "styled-components";

const TextArea = styled.textarea`
  height: 6em;
  width: 100%;
  box-sizing: border-box;
  background: var(--background-color, #707070);
  border: 2px solid var(--border-color, #707070);
  border-radius: 3px;
  font-family: monospace;
  padding: 0.2em;
  margin: 0;
`;
const View = styled.div`
  height: 6em;
  width: 100%;
  box-sizing: border-box;
  background: var(--background-color, #707070);
  border: 1px solid var(--border-color, #707070);
  border-radius: 3px;
  font-family: monospace;
  padding: 0.2em;
  margin: 0;
`;

type Mode = "edit" | "view";
type Props = {
    prompt: string
    setPrompt: (prompt: string) => void
    getParsedPromptOutput: (prompt: string) => JSX.Element
}

export default function PromptEditAndView( props:Readonly<Props> ) {
    const [prompt, setPrompt] = useState<string>("");
    const [mode, setMode] = useState<Mode>("view");

    useEffect(() => {
        setPrompt(props.prompt);
    }, [props.prompt]);

    function onPromptInput( event: ChangeEvent<HTMLTextAreaElement> ) {
        setPrompt(event.target.value);
    }

    function onFinishEdit() {
        setMode("view");
        props.setPrompt(prompt);
    }

    function onFinishView() {
        setMode("edit");
    }

    switch (mode) {
        case "edit": return <TextArea value={prompt} onChange={onPromptInput} onBlur={onFinishEdit}/>;
        case "view": return <View onClick={onFinishView}>{props.getParsedPromptOutput(prompt)}</View>;
    }
}