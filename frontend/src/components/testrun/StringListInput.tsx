import {ChangeEvent, useEffect, useState} from "react";
import styled from "styled-components";

const InputField = styled.input`
    margin-right: 0.5em;
    margin-bottom: 0.2em;
`;

type Props = {
    values: string[]
    fieldSize: number
    onAddValue: (value: string, index: number) => void
    onChangeValue: (value: string, index: number) => void
    allowDeleteValue: (value: string, index: number) => boolean
}

export default function StringListInput( props:Readonly<Props> ) {
    const [ values, setValues ] = useState<string[]>([]);

    useEffect(() => {
        const newValues: string[] = [...props.values];
        newValues.push( "" );
        setValues(newValues);
    }, [props.values]);

    function onInputChange(event: ChangeEvent<HTMLInputElement>, index: number) {
        const value: string = event.target.value;
        if (index == values.length-1)
        {
            const newValues: string[] = [...values];
            newValues[index] = value;
            newValues.push( "" );
            setValues(newValues);
            props.onAddValue(value, index);
        }
        else if (value === "")
        {
            const isAllowed = props.allowDeleteValue(values[index], index);
            if (isAllowed) {
                const newValues: string[] = [...values];
                newValues.splice(index, 1);
                setValues(newValues);
            }
        }
        else
        {
            const newValues: string[] = [...values];
            newValues[index] = value;
            setValues(newValues);
            props.onChangeValue(value, index);
        }
    }

    return (
        <div className={"FlexRow"}>
            {
                values.map(
                    (value, index) =>
                       <InputField
                           key={index}
                           size={props.fieldSize}
                           value={value}
                           onChange={e=>onInputChange(e,index)}
                       />
                )
            }
        </div>
    )
}