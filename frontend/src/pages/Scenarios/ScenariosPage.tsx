import "./ScenariosPage.css";
import {SHOW_RENDERING_HINTS, UserInfos} from "../../models/BaseTypes.tsx";
import axios from "axios";
import {ChangeEvent, useEffect, useState} from "react";
import ScenarioCard from "./components/ScenarioCard.tsx";
import {createDialog} from "../../components/FloatingDialogs.tsx";
import AddScenario from "./components/AddScenario.tsx";
import EditScenario from "./components/EditScenario.tsx";
import DeleteScenario from "./components/DeleteScenario.tsx";
import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import {NewScenario, Scenario, ScenarioDialogOptions} from "../../models/ScenarioTypes.tsx";

type Props = {
    user: UserInfos
}

export default function ScenariosPage(props:Readonly<Props> ) {
    const [ scenarios, setScenarios ] = useState<Scenario[]>([]);
    const [ showAll, setShowAll ] = useState<boolean>(false);
    const { user } = props;
    if (SHOW_RENDERING_HINTS) console.debug("Rendering ScenarioList", { scenarios: scenarios.length });

    useEffect(loadScenarios, [ showAll ]);

    function loadScenarios(){
        const url = showAll ? "/api/scenario/all" : "/api/scenario";
        axios.get(url)
            .then((response) => {
                if (response.status!==200)
                    throw new Error("Get wrong response status, when loading all scenario: "+response.status);
                setScenarios(response.data);
            })
            .catch((error)=>{
                console.error("Error in ScenarioList.loadScenarios():", error);
            })
    }

    function addScenario( label: string ) {
        const newScenario: NewScenario = { label };
        axios.post("/api/scenario", newScenario )
            .then((response) => {
                if (response.status!==200)
                    throw new Error("Get wrong response status, when adding a scenario: "+response.status);
                loadScenarios();
            })
            .catch((error)=>{
                console.error("Error in ScenarioList.addScenario():", error);
            })
    }

    function editScenario( scenario: Scenario ) {
        axios.put(`/api/scenario/${scenario.id}`, scenario )
            .then((response) => {
                if (response.status!==200)
                    throw new Error("Get wrong response status, when updating a scenario: "+response.status);
                loadScenarios();
            })
            .catch((error)=>{
                console.error("Error in ScenarioList.editScenario():", error);
            })
    }

    function deleteScenario( id: string ) {
        axios.delete(`/api/scenario/${id}` )
            .then((response) => {
                if (response.status!==200)
                    throw new Error("Get wrong response status, when deleting a scenario: "+response.status);
                loadScenarios();
            })
            .catch((error)=>{
                console.error("Error in ScenarioList.deleteScenario():", error);
            })
    }

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
                    saveChanges={editScenario}
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
        setShowAll( event.target.checked );
    }

    return (
        <>
            <BreadCrumbs/>
            {
                user.isAdmin &&
                <label><input type="checkbox" checked={showAll} onChange={onShowAllChange}/> of all users</label>
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