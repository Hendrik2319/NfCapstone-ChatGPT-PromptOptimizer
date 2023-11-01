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

const ColoredSpan = styled.span<{ $bgcolor: string }>`
  background: ${props => props.$bgcolor};
`;

type Mode = "edit" | "view";
type Props = {
    prompt: string
    setPrompt: (prompt: string) => void
    getVariables: () => string[]
    getVarColor: (index: number) => string
    setUpdateCallback: ( callback: ()=>void ) =>void
}

export default function PromptEditAndView( props:Readonly<Props> ) {
    const [prompt, setPrompt] = useState<string>("");
    const [mode, setMode] = useState<Mode>("view");

    useEffect(() => {
        setPrompt(props.prompt);
    }, [props.prompt]);


    props.setUpdateCallback( ()=> {
        setMode("view");
    } );

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

    function getParsedPromptOutput(prompt: string): JSX.Element {
        const variables = props.getVariables();
        const parts: (string | number)[] = [];

        while (prompt !== "") {
            let nextVarPos = -1;
            let nextVarIndex = -1;
            for (let i = 0; i < variables.length; i++) {
                const pos = prompt.indexOf("{"+variables[i]+"}");
                if (pos<0) continue;
                if (nextVarPos<0 || nextVarPos>pos) {
                    nextVarPos = pos;
                    nextVarIndex = i;
                }
            }
            if (nextVarPos<0)
            { // no var found
                parts.push(prompt);
                prompt = "";
            }
            else
            { // nearest var found at {nextVarPos}
                parts.push(prompt.substring(0,nextVarPos));
                parts.push(nextVarIndex);
                prompt = prompt.substring( nextVarPos + ("{"+variables[nextVarIndex]+"}").length );
            }
        }

        return (
            <>
                {
                    parts.map( (part, index) => {
                        if (typeof part === "string") return part;
                        return <ColoredSpan key={index} $bgcolor={props.getVarColor(part)}>{"{" + variables[part] + "}"}</ColoredSpan>
                    } )
                }
            </>
        )
    }

    switch (mode) {
        case "edit": return <TextArea value={prompt} onChange={onPromptInput} onBlur={onFinishEdit}/>;
        case "view": return <View onClick={onFinishView}>{getParsedPromptOutput(prompt)}</View>;
    }
}