import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {useState} from "react";
import {Scenario, ScenarioDialogOptions} from "../../../models/ScenarioTypes.tsx";
import styled from "styled-components";

const Dialog = styled.div`
    text-align: right;
`;
const MessageBlock = styled.div`
    margin-bottom: 1em;
`;

type Props = {
    deleteScenario: (id: string) => void
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options: ScenarioDialogOptions)=> void ) => void
}

export default function DeleteScenario( props: Readonly<Props> ) {
    const [ scenario, setScenario ] = useState<Scenario>();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering DeleteScenario",  { scenario: !scenario ? "no scenario" : scenario.id });

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
        <Dialog>
            <MessageBlock>Do you really want to delete Scenario "{scenario.label}"?</MessageBlock>
            <button onClick={deleteScenario}>Yes</button>
            <button onClick={()=>props.closeDialog()}>No</button>
            <button onClick={()=>props.closeDialog()}>Cancel</button>
        </Dialog>
    )
}