import {Link} from "react-router-dom";
import {useEffect, useState} from "react";
import {loadScenario} from "../global_functions/BackendAPI.tsx";
import {Scenario} from "../models/ScenarioTypes.tsx";

type Props = {
    scenarioId?: string
    isNewTestRun?: boolean
}

export default function BreadCrumbs( props:Readonly<Props> ) {
    const [ scenario, setScenario ] = useState<Scenario>();

    useEffect(()=>{
        if (props.scenarioId) {
            loadScenario(props.scenarioId, "BreadCrumbs",scenario=> {
                setScenario(scenario);
            });
        } else
            setScenario(undefined);
    }, [ props.scenarioId ]);


    const scenarioLabel = scenario
        ? "\""+scenario.label+"\""
        : "["+props.scenarioId+"]";

    return (
        <h3 className={"BreadCrumbs"}>
            {
                !props.scenarioId
                    ? "Scenarios"
                    : <Link to={"/"}>Scenarios</Link>
            }
            {
                props.scenarioId && <>
                    {" > "}
                    {
                        !props.isNewTestRun
                            ? scenarioLabel
                            : <Link to={"/scenario/"+props.scenarioId}>{scenarioLabel}</Link>
                    }
                    {
                        props.isNewTestRun &&
                        " > New TestRun"

                    }
                </>
            }
        </h3>
    )
}