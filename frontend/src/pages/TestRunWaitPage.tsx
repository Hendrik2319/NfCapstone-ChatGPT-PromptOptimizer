import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import BreadCrumbs from "../components/BreadCrumbs.tsx";
import {loadRunningTestRuns} from "../global_functions/BackendAPI.tsx";
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
                loadRunningTestRuns(scenarioId, "TestRunWaitPage", setResponseData);
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

    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} isNewTestRun={true}/>
            <p className={"SimpleCard"}>
                Please wait.<br/>
                {
                    runningTestRuns.length==0
                        ? "No TestRuns are" :
                    runningTestRuns.length==1
                        ? "A TestRun is"
                        : "Some TestRuns are"
                }
                in progress ... [idle image]
            </p>
            {
                runningTestRuns.map( (rtr, index) =>
                    <p key={generateKey(rtr, index)} className={"SimpleCard"}>
                        TestRun {rtr.promptIndex+1}/{rtr.totalAmountOfPrompts}<br/>
                        <BigLabel>Label  : </BigLabel>{rtr.label}<br/>
                        <BigLabel>Prompt : </BigLabel>{rtr.prompt}
                    </p>
                )
            }
        </>
    )
}