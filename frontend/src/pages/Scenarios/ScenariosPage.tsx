import "./ScenariosPage.css";
import {SHOW_RENDERING_HINTS, UserInfos} from "../../models/BaseTypes.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import ScenarioCard from "./components/ScenarioCard.tsx";
import {createDialog} from "../../components/FloatingDialogs.tsx";
import AddScenario from "./components/AddScenario.tsx";
import EditScenario from "./components/EditScenario.tsx";
import DeleteScenario from "./components/DeleteScenario.tsx";
import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import {Scenario, ScenarioDialogOptions} from "../../models/ScenarioTypes.tsx";
import {addScenario, deleteScenario, editScenario, loadAllScenarios} from "../../global_functions/BackendAPI.tsx";

type Props = {
    user: UserInfos
}

export default function ScenariosPage(props:Readonly<Props> ) {
    const [ scenarios, setScenarios ] = useState<Scenario[]>([]);
    const [ showFromAllUsers, setShowFromAllUsers ] = useState<boolean>(false);
    const { user } = props;
    if (SHOW_RENDERING_HINTS) console.debug("Rendering ScenarioList", { scenarios: scenarios.length });

    useEffect(loadAllScenarios_, [ showFromAllUsers ]);

    function loadAllScenarios_() { loadAllScenarios( showFromAllUsers, "ScenarioList.loadAllScenarios_()", setScenarios ); }
    function addScenario_   ( label   : string   ) { addScenario   ( label   , "ScenarioList.addScenario_()"   , loadAllScenarios_ ); }
    function editScenario_  ( scenario: Scenario ) { editScenario  ( scenario, "ScenarioList.editScenario_()"  , loadAllScenarios_ ); }
    function deleteScenario_( id      : string   ) { deleteScenario( id      , "ScenarioList.deleteScenario_()", loadAllScenarios_ ); }

    const addDialog =
        createDialog<undefined>(
            'AddScenarioDialog',
            dialogControl =>
                <AddScenario
                    addScenario={addScenario_}
                    closeDialog={dialogControl.closeDialog}
                />
        )

    const editDialog =
        createDialog<ScenarioDialogOptions>(
            'EditScenarioDialog',
            dialogControl =>
                <EditScenario
                    saveChanges={editScenario_}
                    setInitFunction={dialogControl.setInitFunction}
                    closeDialog={dialogControl.closeDialog}
                />
        )

    const deleteDialog =
        createDialog<ScenarioDialogOptions>(
            'DeleteScenarioDialog',
            dialogControl =>
                <DeleteScenario
                    deleteScenario={deleteScenario_}
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
                <button className="ScenarioCard" onClick={()=>addDialog.showDialog()}>Add</button>
                {
                    scenarios.map(
                        scn =>
                            <ScenarioCard
                                key={scn.id}
                                scenario={scn}
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