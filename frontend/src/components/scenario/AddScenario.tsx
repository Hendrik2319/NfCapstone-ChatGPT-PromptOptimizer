import {ChangeEvent, FormEvent, useState} from "react";
import {DEBUG} from "../../Types.tsx";

type Props = {
    addScenario: ( label: string ) => void
    closeDialog: () => void
}

export default function AddScenario( props:Readonly<Props> ) {
    const [ label, setLabel ] = useState<string>("");
    if (DEBUG) console.debug(`Rendering AddScenario {}`)

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
        <form onSubmit={onSubmit}>
            <label>
                Enter a Scenario Label:<br/>
                <input value={label} onChange={onChange}/>
            </label>
            <br/>
            <button>Add</button>
            <button type="button" onClick={closeDialog}>Cancel</button>
        </form>
    )
}