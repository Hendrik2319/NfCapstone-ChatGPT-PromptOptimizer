import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {convertTestRunsFromDTOs, TestRun} from "./Types.tsx";
import {Scenario} from "../scenario/Types.tsx";
import {UserInfos} from "../../Types.tsx";
import TestRunCard from "./TestRunCard.tsx";

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

export default function TestRunsView( props:Readonly<Props> ) {
    const [ scenario, setScenario ] = useState<Scenario>();
    const [ testruns, setTestRuns ] = useState<TestRun[]>([]);
    const { id: scenarioId } = useParams();

    useEffect(()=>{
        if (scenarioId) {
            loadScenario(scenarioId, scenario=> {
                loadTestRuns(scenarioId, testruns => {
                    setScenario(scenario);
                    setTestRuns(testruns);
                });
            });
        }
    }, [ scenarioId ]);

    testruns.sort((t1: TestRun, t2: TestRun): number => {
        if (t1.timestamp < t2.timestamp) return -1;
        if (t1.timestamp > t2.timestamp) return +1;
        if (t1.prompt < t2.prompt) return -1;
        if (t1.prompt > t2.prompt) return +1;
        return 0;
    });

    return (
        <>
            <h3>Scenario "{scenario?.label}"</h3>
            <div>
                <button>Previous TestRuns</button>
                {
                    props.user && scenario && props.user.userDbId === scenario.authorID &&
                    <button>New TestRun</button>
                }
            </div>
            <div className="FlexRowNoWrap">
                {
                    testruns.map( testRun => <TestRunCard key={testRun.id} testRun={testRun}/> )
                }
            </div>
        </>
    )
}