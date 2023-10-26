import {Scenario} from "../../Types.tsx";

type Props = {
    scenario: Scenario
}

export default function ScenarioCard( props:Readonly<Props> ) {
    return (
        <div className={"ScenarioCard"}>
            <div>id       : {props.scenario.id      }</div>
            <div>authorID : {props.scenario.authorID}</div>
            <div>label    : {props.scenario.label   }</div>
        </div>
    )
}