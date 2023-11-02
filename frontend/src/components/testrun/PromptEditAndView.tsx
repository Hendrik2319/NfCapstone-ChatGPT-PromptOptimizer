import {ChangeEvent, useEffect, useState} from "react";
import styled from "styled-components";
import {SHOW_RENDERING_HINTS} from "../../Types.tsx";
import {VariablesChangeMethod} from "./Types.tsx";

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
    getVariables: () => string[]
    getVarColor: (index: number) => string
    updateUsedVars: (usedVars: Set<number>) => void
    onPromptChange: (prompt: string) => void
    setGetter: ( getter: ()=>string ) => void
    setVarChangeNotifier: ( notifier: VariablesChangeMethod )=>void
}

type VarChange = {
    index: number
    oldVarName: string
    newVarName: string
}

export default function PromptEditAndView( props:Readonly<Props> ) {
    const [prompt, setPrompt] = useState<string>(props.prompt);
    const [mode, setMode] = useState<Mode>("view");
    const [varChange, setVarChange] = useState<VarChange>({ index:-1, oldVarName:"", newVarName:"" });
    props.setGetter( ()=>prompt );
    if (SHOW_RENDERING_HINTS) console.debug("Rendering PromptEditAndView");

    useEffect(() => {
        setPrompt(props.prompt);
    }, [props.prompt]);

    props.setVarChangeNotifier( (index: number, oldVarName: string, newVarName: string) => {
        setVarChange({ index, oldVarName, newVarName });
    });

    function onPromptInput( event: ChangeEvent<HTMLTextAreaElement> ) {
        setPrompt(event.target.value);
    }

    function onFinishEdit() {
        setMode("view");
        props.onPromptChange(prompt);
    }
    function onFinishView() {
        setMode("edit");
    }

    function fixVariables(variables: string[]): string[] {
        let copy = Array.from(variables);
        if (varChange.index>=0)
        {
            const oldVarNameIsEmpty = varChange.oldVarName==="";
            const newVarNameIsEmpty = varChange.newVarName === "";
            const oldVarNameIsAtIndex = varChange.index < variables.length && variables[varChange.index] === varChange.oldVarName;

            if ( oldVarNameIsEmpty && ! newVarNameIsEmpty && varChange.index===variables.length)
            { // add
                copy.push(varChange.newVarName);
            }
            else if ( ! oldVarNameIsEmpty && newVarNameIsEmpty && oldVarNameIsAtIndex)
            { // delete
                copy = copy.splice(varChange.index, 1);
            }
            else if ( ! oldVarNameIsEmpty && ! newVarNameIsEmpty && oldVarNameIsAtIndex)
            { // change
                copy[varChange.index] = varChange.newVarName;
            }
        }
        return copy;
    }

    function getParsedPromptOutput(): JSX.Element {
        const variables = fixVariables(props.getVariables());
        const parts: (string | number)[] = [];
        const usedVars = new Set<number>();
        let promptStr = prompt;

        while (promptStr !== "")
        {
            let nextVarPos = -1;
            let nextVarIndex = -1;
            for (let i = 0; i < variables.length; i++) {
                const pos = promptStr.indexOf("{"+variables[i]+"}");
                if (pos<0) continue;
                if (nextVarPos<0 || nextVarPos>pos) {
                    nextVarPos = pos;
                    nextVarIndex = i;
                }
            }

            if (nextVarPos<0)
            { // no var found
                parts.push(promptStr);
                promptStr = "";
            }
            else
            { // nearest var found at {nextVarPos}
                parts.push(promptStr.substring(0,nextVarPos));
                parts.push(nextVarIndex);
                usedVars.add(nextVarIndex);
                promptStr = promptStr.substring( nextVarPos + ("{"+variables[nextVarIndex]+"}").length );
            }
        }

        props.updateUsedVars(usedVars);

        return (
            <>
                {
                    parts.map( (part, index) => {
                        if (typeof part === "string") return part;
                        const key = ""+index;
                        return <ColoredSpan key={key} $bgcolor={props.getVarColor(part)}>{"{" + variables[part] + "}"}</ColoredSpan>
                    } )
                }
            </>
        )
    }

    switch (mode) {
        case "edit": return <TextArea value={prompt} onChange={onPromptInput} onBlur={onFinishEdit}/>;
        case "view": return <View onClick={onFinishView}>{getParsedPromptOutput()}</View>;
    }
}