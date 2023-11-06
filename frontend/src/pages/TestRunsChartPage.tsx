import BreadCrumbs from "../components/BreadCrumbs.tsx";
import {useParams} from "react-router-dom";
import {Chart} from "react-chartjs-2";
import {
    BarController,
    BarElement,
    CategoryScale,
    Chart as ChartJS, ChartData,
    Legend,
    LinearScale,
    LineController,
    LineElement,
    PointElement,
    Title,
    Tooltip
} from "chart.js";
import {SHOW_RENDERING_HINTS} from "../models/BaseTypes.tsx";
import styled from "styled-components";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    PointElement,
    LineElement,
    LineController,
    BarController,
    Title,
    Tooltip,
    Legend
);

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

    const options = {
        responsive: true,
        interaction: {
            mode: 'index' as const,
            intersect: false,
        },
        stacked: false,
        plugins: {
            title: {
                display: true,
                text: 'Chart.js Line Chart - Multi Axis',
            },
        },
        scales: {
            y: {
                type: 'linear' as const,
                display: true,
                position: 'left' as const,
            },
            y1: {
                type: 'linear' as const,
                display: true,
                position: 'right' as const,
                grid: {
                    drawOnChartArea: false,
                },
            },
        },
    };

    const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

    const data: ChartData<"line" | "bar", number[], string> = {
        labels: labels,
        datasets: [
            {
                type: 'line' as const,
                label: 'Dataset 1',
                data: labels.map(() => Math.random()*20 -10),
                borderColor: 'rgb(255, 99, 132)',
                backgroundColor: 'rgba(255, 99, 132, 0.5)',
                borderWidth: 2,
                fill: false,
                yAxisID: 'y',
            },
            {
                type: 'bar' as const,
                label: 'Dataset 2',
                backgroundColor: 'rgb(75, 192, 192)',
                data: labels.map(() => Math.random()*2000 -1000),
                borderColor: 'white',
                borderWidth: 2,
                yAxisID: 'y1',
            },
        ],
        yLabels: [ 'A', 'B']
    };
    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"Chart"}/>
            <SimpleCard>
                <Chart type='bar' data={data} options={options}/>
            </SimpleCard>
        </>
    )
}