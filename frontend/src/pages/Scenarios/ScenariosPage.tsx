import {SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";
import {UserInfo} from "../../models/UserManagementTypes.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import ScenarioCard from "./components/ScenarioCard.tsx";
import {createDialog} from "../../components/FloatingDialogs.tsx";
import AddScenario from "./components/AddScenario.tsx";
import EditScenario from "./components/EditScenario.tsx";
import DeleteScenario from "./components/DeleteScenario.tsx";
import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import {Scenario, ScenarioDialogOptions} from "../../models/ScenarioTypes.tsx";
import {BackendAPI} from "../../global_functions/BackendAPI.tsx";
import styled from "styled-components";

const AddButton = styled.button`
  padding: 1em;
  margin: 0.5em;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: 5px 5px 5px var(--box-shadow-color);
`;

type Props = {
    user: UserInfo
}

export default function ScenariosPage(props:Readonly<Props> ) {
    const [ scenarios, setScenarios ] = useState<Scenario[]>([]);
    const [ showFromAllUsers, setShowFromAllUsers ] = useState<boolean>(false);
    const { user } = props;
    if (SHOW_RENDERING_HINTS) console.debug("Rendering ScenarioList", { scenarios: scenarios.length });

    useEffect(loadAllScenarios, [ showFromAllUsers ]);

    function loadAllScenarios() { BackendAPI.loadAllScenarios( showFromAllUsers, "ScenarioList.loadAllScenarios()", setScenarios ); }
    function addScenario   ( label   : string   ) { BackendAPI.addScenario   ( label   , "ScenarioList.addScenario()"   , loadAllScenarios ); }
    function updateScenario( scenario: Scenario ) { BackendAPI.updateScenario( scenario, "ScenarioList.updateScenario()", loadAllScenarios ); }
    function deleteScenario( id      : string   ) { BackendAPI.deleteScenario( id      , "ScenarioList.deleteScenario()", loadAllScenarios ); }

    const addDialog =
        createDialog<undefined>(
            'AddScenarioDialog',
            dialogControl =>
                <AddScenario
                    addScenario={addScenario}
                    closeDialog={dialogControl.closeDialog}
                />
        )

    const editDialog =
        createDialog<ScenarioDialogOptions>(
            'EditScenarioDialog',
            dialogControl =>
                <EditScenario
                    saveChanges={updateScenario}
                    setInitFunction={dialogControl.setInitFunction}
                    closeDialog={dialogControl.closeDialog}
                />
        )

    const deleteDialog =
        createDialog<ScenarioDialogOptions>(
            'DeleteScenarioDialog',
            dialogControl =>
                <DeleteScenario
                    deleteScenario={deleteScenario}
                    setInitFunction={dialogControl.setInitFunction}
                    closeDialog={dialogControl.closeDialog}
                />
        )

    function onShowAllChange( event: ChangeEvent<HTMLInputElement> ) {
        setShowFromAllUsers( event.target.checked );
    }

    return (
        <>
            <BreadCrumbs/>
            {
                user.isAdmin &&
                <label><input type="checkbox" checked={showFromAllUsers} onChange={onShowAllChange}/> of all users</label>
            }
            <div className="FlexRow">
                <AddButton onClick={()=>addDialog.showDialog()}>Add</AddButton>
                {
                    scenarios.map(
                        scn =>
                            <ScenarioCard
                                key={scn.id}
                                scenario={scn}
                                user={user}
                                showEditDialog={editDialog.showDialog}
                                showDeleteDialog={deleteDialog.showDialog}
                            />
                    )
                }
            </div>
            {addDialog.writeHTML()}
            {editDialog.writeHTML()}
            {deleteDialog.writeHTML()}
        </>
    )
}