import StringListInput from "./StringListInput.tsx";
import {useEffect, useState} from "react";
import styled from "styled-components";
import {SHOW_RENDERING_HINTS} from "../../../Types.tsx";
import {VariablesChangeMethod} from "../Types.tsx";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

type Props = {
    variables: string[]
    isAllowedToDelete: (varName: string) => boolean
    getVarColor: (index: number) => string
    notifyOthersAboutChange: VariablesChangeMethod
    onVariablesChange: (variables: string[]) => void
    setGetter: ( getter: ()=>string[] ) => void
}

export default function VariablesEdit( props: Readonly<Props> ) {
    const [variables, setVariables] = useState<string[]>(props.variables);
    props.setGetter( ()=>variables );
    if (SHOW_RENDERING_HINTS) console.debug("Rendering VariablesEdit");

    useEffect(() => {
        setVariables(props.variables)
    }, [props.variables]);

    function changeVariable( changeAction: (changedVariables: string[]) => void ) {
        const changedVariables = [...variables];
        changeAction(changedVariables);
        props.onVariablesChange( changedVariables);
        setVariables(changedVariables);
    }

    function checkName(varName: string, index: number, errorMsg1: string, errorMsg2: string) {
        if (varName.includes("{") || varName.includes("}")) {
            alert( errorMsg1+"\r\nCharacters like '{' or '}' are not allowed in variable names.");
            return false;
        }

        const pos = variables.indexOf(varName);
        if (0<=pos && pos!==index) {
            alert( errorMsg1+"\r\n"+errorMsg2+" will be equal to variable "+ (pos + 1) +".");
            return false;
        }
        return true;
    }

    function allowAddVariable(value: string, index: number) {
        const isAllowed = checkName(value,index,"Can't add variable with this name.", "New variable");
        if (isAllowed) {
            changeVariable( changedVariables => changedVariables.push(value));
            props.notifyOthersAboutChange(index, "", value);
        }
        return isAllowed;
    }

    function allowChangeVariable(value: string, index: number) {
        const isAllowed = checkName(value, index, "Can't change variable to this name.","Variable "+ (index+1));
        if (isAllowed) {
            const oldValue = variables[index];
            changeVariable( changedVariables => changedVariables[index] = value);
            props.notifyOthersAboutChange(index, oldValue, value);
        }
        return isAllowed;
    }

    function allowDeleteVariable(value: string, index: number): boolean {
        if (!props.isAllowedToDelete(value)) return false;
        changeVariable(changedVariables => changedVariables.splice(index, 1));
        props.notifyOthersAboutChange(index, value, "");
        return true;
    }

    return (
        <SimpleCard>
            <StringListInput
                values={variables}
                fieldSize={10}
                getFieldBgColor={props.getVarColor}
                allowAddValue   ={allowAddVariable   }
                allowChangeValue={allowChangeVariable}
                allowDeleteValue={allowDeleteVariable}
            />
        </SimpleCard>
    )
}