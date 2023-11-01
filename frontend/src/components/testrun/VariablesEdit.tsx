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

    function onAddVariable(value: string) {
        changeVariable( changedVariables => changedVariables.push(value));
    }

    function onChangeVariable(value: string, index: number) {
        changeVariable( changedVariables => changedVariables[index] = value);
    }

    function allowDeleteVariable(value: string, index: number): boolean {
        changeVariable( changedVariables => changedVariables.splice(index, 1));
        return true;
    }

    return (
        <SimpleCard>
            <StringListInput
                values={variables}
                fieldSize={10}
                getFieldBgColor={props.getVarColor}
                onAddValue      ={onAddVariable}
                onChangeValue   ={onChangeVariable}
                allowDeleteValue={allowDeleteVariable}
            />
        </SimpleCard>
    )
}