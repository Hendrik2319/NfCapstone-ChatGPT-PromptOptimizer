import {ChangeEvent, FormEvent, useState} from "react";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import styled from "styled-components";

const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  
  & > div {
    text-align: right;
  }
  & > * {
    margin: 0.5em 0;
  }
  & > *:first-child { margin-top: 0; }
  & > *:last-child  { margin-bottom: 0; }
`;

type Props = {
    addScenario: ( label: string ) => void
    closeDialog: () => void
}

export default function AddScenario( props:Readonly<Props> ) {
    const [ label, setLabel ] = useState<string>("");
    if (SHOW_RENDERING_HINTS) console.debug("Rendering AddScenario")

    function onChange( event: ChangeEvent<HTMLInputElement> ) {
        setLabel(event.target.value);
    }

    function onSubmit( event: FormEvent<HTMLFormElement> ) {
        event.preventDefault();
        if (label.length !== 0) {
            props.addScenario(label);
            closeDialog();
        } else {
            alert("Please enter a label before adding.");
        }
    }

    function closeDialog() {
        props.closeDialog();
        setLabel("");
    }

    return (
        <StyledForm onSubmit={onSubmit}>
            <label>Enter a Scenario Label:</label>
            <input value={label} onChange={onChange}/>
            <div>
                <button>Add</button>
                <button type="button" onClick={closeDialog}>Cancel</button>
            </div>
        </StyledForm>
    )
}