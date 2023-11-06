import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import BreadCrumbs from "../components/BreadCrumbs.tsx";
import {loadRunningTestRunsOfScenario} from "../global_functions/BackendAPI.tsx";
import {RunningTestRun} from "../models/TestRunTypes.tsx";
import {BigLabel} from "../components/StandardStyledComponents.tsx";

export default function TestRunWaitPage() {
    const [ runningTestRuns, setRunningTestRuns ] = useState<RunningTestRun[]>([]);
    const { id: scenarioId } = useParams();

    useEffect(() => {
        console.debug("TestRunWaitPage.timeout: starting ( scenarioId:"+scenarioId+" )");

        let stopNow = false;
        let timeoutID: number = -1;
        let counter = 0;

        function setResponseData(runningTestRuns: RunningTestRun[]) {
            setRunningTestRuns(runningTestRuns);
            if (!stopNow)
                timeoutID = setTimeout(loop, 500);
        }

        function loop() {
            console.debug("TestRunWaitPage.interval: call "+(counter++)+" ( scenarioId:"+scenarioId+" )");

            if (scenarioId)
                loadRunningTestRunsOfScenario(scenarioId, "TestRunWaitPage", setResponseData);
        }

        loop();

        return () => {
            stopNow = true;
            clearTimeout(timeoutID);
            console.debug("TestRunWaitPage.timeout: cleared ( scenarioId:"+scenarioId+" )");
        };
    }, [ scenarioId ]);

    if (!scenarioId)
        return (
            <>
                Please wait.<br/>
                A TestRun is in progress ...
            </>
        )

    function generateKey(rtr: RunningTestRun, index: number) {
        return index +"_"+ rtr.promptIndex +"_"+ rtr.totalAmountOfPrompts +"_"+ rtr.label +"_"+ rtr.prompt;
    }

    function getFirstPart(length: number) {
        if (length == 0) return "No TestRuns are ";
        if (length == 1) return "A TestRun is ";
        return "Some TestRuns are ";
    }

    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"New TestRun"}/>
            <p className={"SimpleCard"}>
                Please wait.<br/>
                { getFirstPart(runningTestRuns.length) } in progress ...
            </p>
            {
                runningTestRuns.map( (rtr, index) =>
                    <div key={generateKey(rtr, index)} className="SimpleCard">
                        TestRun {rtr.promptIndex+1}/{rtr.totalAmountOfPrompts}<br/>
                        <progress value={rtr.promptIndex} max={rtr.totalAmountOfPrompts} >
                            {rtr.promptIndex+1}/{rtr.totalAmountOfPrompts}
                        </progress><br/>
                        <BigLabel>Label  : </BigLabel>
                        <div className="SimpleCard">{rtr.label}</div>
                        <BigLabel>Prompt : </BigLabel>
                        <div className="SimpleCard">{rtr.prompt}</div>
                    </div>
                )
            }
        </>
    )
}