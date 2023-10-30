import {Scenario, ScenarioDialogOptions} from "./Types.tsx";
import {useNavigate} from "react-router-dom";
import {Id, Label} from "../StandardStyledComponents.tsx";
import styled from "styled-components";

type Props = {
    scenario: Scenario
    showEditDialog: ( options: ScenarioDialogOptions ) => void
    showDeleteDialog: ( options: ScenarioDialogOptions ) => void
}

const BaseCard = styled.div`
  padding: 1em;
  margin: 0.5em;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: 5px 5px 5px var(--box-shadow-color);
`;

const ContentButton = styled.button`
  text-align: left;
  width: 100%;
`;

export default function ScenarioCard( props:Readonly<Props> ) {
    const navigate = useNavigate();

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
        <BaseCard>
            <ContentButton onClick={()=>navigate("/scenario/"+props.scenario.id)}>
                <Id>id: {props.scenario.id}</Id>
                <div><Label>label    :</Label>{props.scenario.label   }</div>
                <div><Label>authorID :</Label>{props.scenario.authorID}</div>
            </ContentButton>
            <br/>
            <button onClick={editScenario}>Change Name</button>
            <button onClick={deleteScenario}>Delete</button>
        </BaseCard>
    )
}