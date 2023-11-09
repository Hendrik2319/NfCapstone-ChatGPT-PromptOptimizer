import {Link} from "react-router-dom";
import {BigLabel, Id} from "../../../components/StandardStyledComponents.tsx";
import styled from "styled-components";
import {Scenario, ScenarioDialogOptions} from "../../../models/ScenarioTypes.tsx";
import {SVGsInVars} from "../../../assets/SVGsInVars.tsx";
import {UserInfo} from "../../../models/UserManagementTypes.tsx";

const BaseCard = styled.div`
  padding: 1em;
  margin: 0.5em;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: 5px 5px 5px var(--box-shadow-color);
  
  a {
    text-decoration: none;
    color: var(--text-color);
  }
`;

const ScenarioTitle = styled.div`
  font-size: 1.2em;
  font-weight: bold;
  margin: 0.5em 0;
`;

const ButtonImage = styled.div`
  display: inline-block;
  vertical-align: middle;
  min-width: 1em;
`;

type Props = {
    scenario: Scenario
    user: UserInfo
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
        <BaseCard>
            <Link to={"/scenario/"+props.scenario.id}>
                <Id>id: {props.scenario.id}</Id>
                <ScenarioTitle>{props.scenario.label}</ScenarioTitle>
                {
                    props.user.userDbId !== props.scenario.authorID &&
                    <div><BigLabel>Author : </BigLabel>{props.scenario.authorID}</div>
                }
            </Link>
            <br/>
            <button onClick={editScenario}><ButtonImage>{ SVGsInVars.Edit }</ButtonImage>Change Name</button>
            <button onClick={deleteScenario}>Delete</button>
        </BaseCard>
    )
}