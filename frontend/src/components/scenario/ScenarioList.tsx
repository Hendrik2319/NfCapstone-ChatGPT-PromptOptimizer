import "./ScenarioList.css";
import {NewScenario, Scenario} from "../../Types.tsx";
import axios from "axios";
import {useEffect, useState} from "react";
import ScenarioCard from "./ScenarioCard.tsx";
import {createDialog} from "../FloatingDialogs.tsx";
import AddScenario from "./AddScenario.tsx";

export default function ScenarioList() {
    const [ scenarios, setScenarios ] = useState<Scenario[]>([]);
    console.debug(`Rendering ScenarioList { scenarios: [${scenarios.length}] }`);

    useEffect(() => {
        loadScenarios()
    }, []);

    function loadScenarios(){
        axios.get("/api/scenario")
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

    const addDialog =
        createDialog<undefined>(
            'addScenarioDialog',
            dialogControl =>
                <AddScenario addScenario={addScenario} closeDialog={dialogControl.closeDialog}/>
        )

    return (
        <>
            Scenarios
            <div className={"ScenarioList"}>
                <button className={"ScenarioCard"} onClick={()=>addDialog.showDialog()}>Add</button>
                {
                    scenarios.map(scn => <ScenarioCard key={scn.id} scenario={scn}/>)
                }
            </div>
            {addDialog.writeHTML()}
        </>
    )
}