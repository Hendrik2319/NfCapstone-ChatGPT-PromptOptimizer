import {Scenario} from "../../Types.tsx";
import {ScenarioDialogOptions} from "./Types.tsx";

type Props = {
    scenario: Scenario
    showEditDialog: ( options: ScenarioDialogOptions ) => void
    showDeleteDialog: ( options: ScenarioDialogOptions ) => void
}

export default function ScenarioCard( props:Readonly<Props> ) {

    function editScenario() {
        props.showEditDialog( {
            scenario: props.scenario
        });
    }

    function deleteScenario() {
        props.showDeleteDialog({
            scenario: props.scenario
        });
    }

    return (
        <div className={"ScenarioCard"}>
            <div>id       : {props.scenario.id      }</div>
            <div>authorID : {props.scenario.authorID}</div>
            <div>label    : {props.scenario.label   }</div>
            <button onClick={editScenario}>Change Name</button>
            <button onClick={deleteScenario}>Delete</button>
        </div>
    )
}