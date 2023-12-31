import "./TestRunsPage.css";
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";
import {isCurrentNewTestRunStored, saveCurrentNewTestRun} from "../../global_functions/NewTestRunStoarage.tsx";
import {BackendAPI} from "../../global_functions/BackendAPI.tsx";
import TestRunsList from "./components/TestRunsList.tsx";
import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import {Scenario} from "../../models/ScenarioTypes.tsx";
import {TestRun} from "../../models/TestRunTypes.tsx";
import {UserInfo} from "../../models/UserManagementTypes.tsx";
import {ButtonSVG, SimpleCard} from "../../components/StandardStyledComponents.tsx";
import styled from "styled-components";
import {SVGsInVars} from "../../assets/SVGsInVars.tsx";

const LocalNavBar = styled(SimpleCard)`
  padding: 0.3em;
  
  & > * {
    margin-left: 1em;
  }
`;

type Props = {
    user?: UserInfo
}

export default function TestRunsPage( props:Readonly<Props> ) {
    const [ scenario, setScenario ] = useState<Scenario>();
    const [ testruns, setTestruns ] = useState<TestRun[]>([]);
    const { id: scenarioId } = useParams();
    const navigate = useNavigate();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestRunsView", { scenarioId });

    useEffect(()=>{
        if (scenarioId) {
            BackendAPI.loadScenarioById(scenarioId, "TestRunsView.useEffect", scenario=> {
                BackendAPI.loadTestRunsOfScenario(scenarioId, "TestRunsView.useEffect", testruns => {
                    setScenario(scenario);
                    setTestruns(testruns);
                });
            });
        }
    }, [ scenarioId ]);

    if (!scenarioId || !scenario) {
        if (!scenarioId) navigate("/");
        return <>No Scenario found</>
    }

    function saveChangedScenario( newData: Scenario ) {
        BackendAPI.updateScenario(newData, "TestRunsView.saveChangedScenario", setScenario);
    }

    const userCanStartNewTestRun =
        props.user && scenario &&
        props.user.userDbId === scenario.authorID;

    const currentNewTestRunIsStored = isCurrentNewTestRunStored(scenarioId);

    testruns.sort((t1: TestRun, t2: TestRun): number => {
        if (t1.timestamp < t2.timestamp) return -1;
        if (t1.timestamp > t2.timestamp) return +1;
        if (t1.prompt < t2.prompt) return -1;
        if (t1.prompt > t2.prompt) return +1;
        return 0;
    });

    function startNewTestRunFromList( base: TestRun, scenarioId: string ) {
        saveCurrentNewTestRun(scenarioId, {
           scenarioId,
           prompt: base.prompt,
           variables: base.variables,
           testcases: base.testcases
        });
        navigate("/scenario/"+scenarioId+"/newtestrun")
    }

    return (
        <>
            <BreadCrumbs scenarioId={scenarioId}/>
            <LocalNavBar>
                <button onClick={()=>navigate("/scenario/"+scenarioId+"/chart")}>Optimization Chart <ButtonSVG>{ SVGsInVars.Chart }</ButtonSVG></button>
                {
                    userCanStartNewTestRun && (currentNewTestRunIsStored || testruns.length===0) &&
                    <button onClick={()=>navigate("/scenario/"+scenarioId+"/newtestrun")}>New TestRun</button>
                }
            </LocalNavBar>
            <br/>
            <TestRunsList
                scenario={scenario}
                testruns={testruns}
                startNewTestRun={
                    userCanStartNewTestRun
                        ? base => startNewTestRunFromList( base, scenarioId )
                        : undefined
                }
                saveChangedScenario={saveChangedScenario}
            />
        </>
    )
}