import {ChangeEvent, ReactElement, useEffect, useState} from "react";
import styled from "styled-components";

const InputField = styled.input<{ $bgcolor: string }>`
  margin-right: 0.5em;
  margin-bottom: 0.2em;
  background: ${props => props.$bgcolor};
  border: 1px solid var(--border-color, #707070);
  border-radius: 3px;
  color: var(--text-color);
  font-size: 1em;
  padding: 0.2em 0.5em;
`;

type Props = {
    labelComp?: ReactElement
    values: string[]
    fieldSize: number
    allowAddValue   : (value: string, index: number) => boolean
    allowChangeValue: (value: string, index: number) => boolean
    allowDeleteValue: (value: string, index: number) => boolean
    getFieldBgColor?: (index: number) => string
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
            const isAllowed = props.allowAddValue(value, index);
            if (isAllowed) {
                const newValues: string[] = [...values];
                newValues[index] = value;
                newValues.push("");
                setValues(newValues);
            }
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
            const isAllowed = props.allowChangeValue(value, index);
            if (isAllowed) {
                const newValues: string[] = [...values];
                newValues[index] = value;
                setValues(newValues);
            }
        }
    }

    function generateKey(index: number) {
        return index;
    }

    return (
        <div className={"FlexRow"}>
            {props.labelComp}
            {
                values.map(
                    (value, index) =>
                       <InputField
                           $bgcolor={props.getFieldBgColor && index+1!==values.length ? props.getFieldBgColor(index) : 'var(--textarea-background-color)' }
                           key={generateKey(index)}
                           size={props.fieldSize}
                           value={value}
                           onChange={e=>onInputChange(e,index)}
                       />
                )
            }
        </div>
    )
}