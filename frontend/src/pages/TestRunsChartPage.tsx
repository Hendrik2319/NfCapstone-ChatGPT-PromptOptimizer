import BreadCrumbs from "../components/BreadCrumbs.tsx";
import {useParams} from "react-router-dom";
import {Line} from "react-chartjs-2";

export default function TestRunsChartPage() {
    const { id: scenarioId } = useParams();

    if (!scenarioId)
        return <>No scenario choosen.</>

    const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

    const data = {
        labels,
        datasets: [
            {
                label: 'Dataset 1',
                data: labels.map(() => Math.random()*2000 -1000),
                borderColor: 'rgb(255, 99, 132)',
                backgroundColor: 'rgba(255, 99, 132, 0.5)',
                yAxisID: 'y',
            },
            {
                label: 'Dataset 2',
                data: labels.map(() => Math.random()*2000 -1000),
                borderColor: 'rgb(53, 162, 235)',
                backgroundColor: 'rgba(53, 162, 235, 0.5)',
                yAxisID: 'y1',
            },
        ],
    };
    return (
        <>
            <BreadCrumbs scenarioId={scenarioId}/>
            <Line data={data}/>
        </>
    )
}