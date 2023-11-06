import BreadCrumbs from "../components/BreadCrumbs.tsx";
import {useParams} from "react-router-dom";

export default function TestRunsChartPage() {
    const { id: scenarioId } = useParams();

    if (!scenarioId)
        return <>No scenario choosen.</>

    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"Chart"}/>
            Content
        </>
    )
}