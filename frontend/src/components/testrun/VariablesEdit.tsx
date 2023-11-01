import StringListInput from "./StringListInput.tsx";
import {useEffect, useState} from "react";
import styled from "styled-components";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

type Props = {
    variables: string[]
    getVarColor: (index: number) => string
    saveFormValues: (variables: string[]) => void
    setGetter: ( getter: ()=>string[] ) => void
}

export default function VariablesEdit( props: Readonly<Props> ) {
    const [variables, setVariables] = useState<string[]>(props.variables);
    props.setGetter( ()=>variables );

    useEffect(() => {
        setVariables(props.variables)
    }, [props.variables]);

    function changeVariable( changeAction: (changedVariables: string[]) => void ) {
        const changedVariables = [...variables];
        changeAction(changedVariables);
        props.saveFormValues( changedVariables);
        setVariables(changedVariables);
    }

    function allowAddVariable(value: string) {
        const pos = variables.indexOf(value);
        if (0<=pos) {
            alert("Can't add variable with this value.\r\nNew variable will be equal to variable " + (pos + 1) + ".");
            return false;
        }
        changeVariable( changedVariables => changedVariables.push(value));
        return true;
    }

    function allowChangeVariable(value: string, index: number) {
        const pos = variables.indexOf(value);
        if (0<=pos && pos!==index) {
            alert("Can't change variable to this value.\r\nVariable " + (index + 1) + " will be equal to variable " + (pos + 1) + ".");
            return false;
        }
        changeVariable( changedVariables => changedVariables[index] = value);
        return true;
    }

    function allowDeleteVariable(value: string, index: number): boolean {
        changeVariable( changedVariables => changedVariables.splice(index, 1));
        // TODO: is var delete allowed
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