import {Scenario} from "../../Types.tsx";
import {EditScenarioOptions} from "./EditScenario.tsx";

type Props = {
    scenario: Scenario
    showEditDialog: ( options: EditScenarioOptions ) => void
}

export default function ScenarioCard( props:Readonly<Props> ) {

    function editScenario() {
        props.showEditDialog( {
            scenario: props.scenario
        });
    }

    return (
        <div className={"ScenarioCard"}>
            <div>id       : {props.scenario.id      }</div>
            <div>authorID : {props.scenario.authorID}</div>
            <div>label    : {props.scenario.label   } <button onClick={editScenario}>Change</button></div>
        </div>
    )
}