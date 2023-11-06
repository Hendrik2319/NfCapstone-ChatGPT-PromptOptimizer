import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import {useParams} from "react-router-dom";
import {SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";
import styled from "styled-components";
import TestRunsChart from "./components/TestRunsChart.tsx";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

export default function TestRunsChartPage() {
    const { id: scenarioId } = useParams();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestRunsChartPage", { scenarioId });

    if (!scenarioId)
        return <>No scenario choosen.</>


    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"Chart"}/>
            <SimpleCard>
                <TestRunsChart/>
            </SimpleCard>
        </>
    )
}