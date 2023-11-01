import {SHOW_RENDERING_HINTS} from "../../Types.tsx";
import {Scenario, ScenarioDialogOptions} from "./Types.tsx";
import {useState} from "react";

type Props = {
    deleteScenario: (id: string) => void
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options: ScenarioDialogOptions)=> void ) => void
}

export default function DeleteScenario( props: Readonly<Props> ) {
    const [ scenario, setScenario ] = useState<Scenario>();
    if (SHOW_RENDERING_HINTS) console.debug(`Rendering DeleteScenario { ${!scenario ? 'no scenario' : 'scenario: ' + scenario.id} }`);

    props.setInitFunction((options: ScenarioDialogOptions) => {
        setScenario(options.scenario);
    });

    function deleteScenario() {
        if (scenario) {
            props.deleteScenario(scenario.id);
            props.closeDialog();
        }
    }

    if (!scenario)
        return <>Loading</>

    return (
        <>
            <div>Do you really want to delete Scenario "{scenario.label}"?</div>
            <button onClick={deleteScenario}>Yes</button>
            <button onClick={()=>props.closeDialog()}>No</button>
            <button onClick={()=>props.closeDialog()}>Cancel</button>
        </>
    )
}