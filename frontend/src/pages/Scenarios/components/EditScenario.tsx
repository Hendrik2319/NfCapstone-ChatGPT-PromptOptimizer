import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {ChangeEvent, FormEvent, useState} from "react";
import {Scenario, ScenarioDialogOptions} from "../../../models/ScenarioTypes.tsx";
import styled from "styled-components";

const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  
  .right { text-align: right; }
  .left  { text-align: left; }

  & > * {
    margin: 0.5em 0;
  }
  & > *:first-child { margin-top: 0; }
  & > *:last-child  { margin-bottom: 0; }
`;

const InputField = styled.input`
  background: var(--textarea-background-color);
  border: 1px solid var(--border-color, #707070);
  border-radius: 3px;
  color: var(--text-color);
  font-size: 1em;
  padding: 0.2em 0.5em;
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
            <div className="left">Enter a Scenario Label :</div>
            <InputField value={label} onChange={onChange}/>
            <div className="right">
                <button>Set</button>
                <button type="button" onClick={closeDialog}>Cancel</button>
            </div>
        </StyledForm>
    )
}
