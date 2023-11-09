import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {ChangeEvent, FormEvent, useState} from "react";
import {Scenario, ScenarioDialogOptions} from "../../../models/ScenarioTypes.tsx";
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
    saveChanges: (scenario: Scenario) => void
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options: ScenarioDialogOptions)=> void ) => void
}

export default function EditScenario( props: Readonly<Props> ) {
    const [ label, setLabel ] = useState<string>("");
    const [ scenario, setScenario ] = useState<Scenario>();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering EditScenario");

    props.setInitFunction((options: ScenarioDialogOptions) => {
        setScenario(options.scenario);
        setLabel(options.scenario.label);
    });

    function onChange( event: ChangeEvent<HTMLInputElement> ) {
        setLabel(event.target.value);
    }

    function onSubmit( event: FormEvent<HTMLFormElement> ) {
        event.preventDefault();
        if (scenario && label.length !== 0) {
            const changedScenario: Scenario = { ...scenario, label };
            props.saveChanges(changedScenario);
            closeDialog();
        } else {
            alert("Please enter a label.");
        }
    }

    function closeDialog() {
        props.closeDialog();
    }

    return (
        <StyledForm onSubmit={onSubmit}>
            <label>Enter a Scenario Label :</label>
            <input value={label} onChange={onChange}/>
            <div>
                <button>Set</button>
                <button type="button" onClick={closeDialog}>Cancel</button>
            </div>
        </StyledForm>
    )
}
