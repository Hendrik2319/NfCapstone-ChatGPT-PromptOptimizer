import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {convertTestRunsFromDTOs, TestRun} from "./Types.tsx";
import {Scenario} from "../scenario/Types.tsx";
import {SHOW_RENDERING_HINTS, UserInfos} from "../../Types.tsx";
import NewTestRunPanel from "./newtestrun/NewTestRunPanel.tsx";
import TestRunsList from "./TestRunsList.tsx";

function loadScenario( scenarioId: string, callback: (scenario: Scenario)=>void ){
    axios.get(`/api/scenario/${scenarioId}`)
        .then((response) => {
            if (response.status!==200)
                throw new Error(`Get wrong response status, when loading scenario (id:${scenarioId}): ${response.status}`);
            callback(response.data)
        })
        .catch((error)=>{
            console.error("ERROR[TestRunsView.loadTestRuns]", error);
        })
}

function loadTestRuns( scenarioId: string, callback: (testruns: TestRun[])=>void ){
    axios.get(`/api/scenario/${scenarioId}/testrun`)
        .then((response) => {
            if (response.status!==200)
                throw new Error(`Get wrong response status, when loading test runs: ${response.status}`);
            callback(convertTestRunsFromDTOs(response.data))
        })
        .catch((error)=>{
            console.error("ERROR[TestRunsView.loadTestRuns]", error);
        })
}

type Props = {
    user?: UserInfos
}

type TabState = "PrevTestRuns" | "NewTestRun"

export default function TestRunsView( props:Readonly<Props> ) {
    const [ scenario, setScenario ] = useState<Scenario>();
    const [ testruns, setTestruns ] = useState<TestRun[]>([]);
    const [ tabState, setTabState ] = useState<TabState>("PrevTestRuns");
    const { id: scenarioId } = useParams();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestRunsView", { scenarioId });

    const userCanStartNewTestRun =
        props.user && scenario &&
        props.user.userDbId === scenario.authorID;

    useEffect(()=>{
        if (scenarioId) {
            loadScenario(scenarioId, scenario=> {
                loadTestRuns(scenarioId, testruns => {
                    setScenario(scenario);
                    setTestruns(testruns);
                });
            });
        }
    }, [ scenarioId ]);

    useEffect(() => {
        if (!userCanStartNewTestRun && tabState==="NewTestRun")
            setTabState("PrevTestRuns")
    }, [tabState, userCanStartNewTestRun]);

    function onSuccessfulTestRun() {
        if (scenarioId)
            loadTestRuns(scenarioId, testruns => {
                setTestruns(testruns);
                setTabState("PrevTestRuns");
            });
    }

    testruns.sort((t1: TestRun, t2: TestRun): number => {
        if (t1.timestamp < t2.timestamp) return -1;
        if (t1.timestamp > t2.timestamp) return +1;
        if (t1.prompt < t2.prompt) return -1;
        if (t1.prompt > t2.prompt) return +1;
        return 0;
    });

    const last: TestRun | undefined = testruns.length===0 ? undefined : testruns[testruns.length-1];

    return (
        <>
            <h3>Scenario "{scenario?.label}"</h3>
            <div>
                <button onClick={()=>setTabState("PrevTestRuns")}>Previous TestRuns</button>
                {
                    userCanStartNewTestRun &&
                    <button onClick={()=>setTabState("NewTestRun")}>New TestRun</button>
                }
            </div>
            {
                tabState==="PrevTestRuns" && scenarioId &&
                <TestRunsList testruns={testruns} scenarioId={scenarioId}/>
            }
            {
                tabState==="NewTestRun" && scenarioId &&
                <NewTestRunPanel previous={last} scenarioId={scenarioId} onSuccessfulTestRun={onSuccessfulTestRun}/>
            }
        </>
    )
}