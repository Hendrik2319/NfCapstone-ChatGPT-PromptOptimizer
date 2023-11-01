import {SHOW_RENDERING_HINTS} from "../../Types.tsx";
import {ChangeEvent, FormEvent, useState} from "react";
import {Scenario, ScenarioDialogOptions} from "./Types.tsx";

type Props = {
    saveChanges: (scenario: Scenario) => void
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options: ScenarioDialogOptions)=> void ) => void
}

export default function EditScenario( props: Readonly<Props> ) {
    const [ label, setLabel ] = useState<string>("");
    const [ scenario, setScenario ] = useState<Scenario>();
    if (SHOW_RENDERING_HINTS) console.debug(`Rendering EditScenario {}`);

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
            alert("Please enter a label before adding.");
        }
    }

    function closeDialog() {
        props.closeDialog();
    }

    return (
        <form onSubmit={onSubmit}>
            <label>
                Enter a Scenario Label:<br/>
                <input value={label} onChange={onChange}/>
            </label>
            <br/>
            <button>Set</button>
            <button type="button" onClick={closeDialog}>Cancel</button>
        </form>
    )
}
