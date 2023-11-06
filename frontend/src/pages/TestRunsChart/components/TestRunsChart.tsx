import {useEffect, useState} from "react";
import {DarkModeState, getCurrentDarkModeState} from "../../Main/components/DarkModeSwitch.Functions.tsx";
import {addAppThemeListener, removeAppThemeListener} from "../../../global_functions/AppThemeListener.tsx";
import {Chart} from "react-chartjs-2";
import {
    BarController,
    BarElement,
    CategoryScale,
    Chart as ChartJS,
    ChartData,
    Legend,
    LinearScale,
    LineController,
    LineElement,
    PointElement,
    Title,
    Tooltip
} from "chart.js";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";

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

type TType = "line" | "bar";
type TOptions = any;
// type TOptions = (
//     CoreChartOptions<TType>
//     & ElementChartOptions<TType>
//     & PluginChartOptions<TType>
//     & DatasetChartOptions<TType>
//     & ScaleChartOptions<TType>
// )

type Props = {
    labels: string[]
    averageTokensPerRequest: number[]
    amountOfAnswersMeetMaxWordCount: number[]
}

export default function TestRunsChart( props:Readonly<Props> ) {
    const [ appTheme, setAppTheme ] = useState<DarkModeState>( getCurrentDarkModeState() )
    if (SHOW_RENDERING_HINTS) console.debug("Rendering TestRunsChart", { appTheme });

    useEffect(() => {
        addAppThemeListener(setAppTheme);
        return ()=> removeAppThemeListener(setAppTheme)
    }, []);

    function select<T>( valueDark: T, valueLight: T) {
        switch (appTheme) {
            case "dark" : return valueDark;
            case "light": return valueLight;
        }
    }

    const colorChartTitle = select( "#d0d0d0", "#000000");
    const colorTickLabels = select( "#d0d0d0", "#000000");
    const colorAxisLabelLineset = "#e87500";
    const colorAxisLabelBarset = select( "#80b0FF", "#5080FF");
    const colorAxisLabelX = select( "#d0d0d0", "#000000");
    const colorLegendText = select( "#d0d0d0", "#000000");
    const colorGrid = select( "#606060", "#a0a0a0");
    const colorAxisLine = select( "#d0d0d0", "#000000");
    const colorLinesetLine = '#e87500';
    const colorLinesetFill = '#e87500';
    const colorBarsetOutline = select( "#d0d0d0", "#000000");
    const colorBarsetFill = select( "#80b0FF", "#5080FF");

    const textChartTitle = 'Values of Answers in TestRun';
    const labelLineset = 'Average Tokens per Request';
    const labelBarset = 'Answer meets Max. Word Count';
    const labelAxisX = "TestRuns";
    const labelAxisLineset = "Tokens per Request";
    const labelAxisBarset = "Amount of Answers, that meet Max. Word Count (%)";

    const labels = props.labels // ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

    const dataLineset = props.averageTokensPerRequest; // labels.map(() => Math.random()*20 -10);
    const dataBarset = props.amountOfAnswersMeetMaxWordCount; // labels.map(() => Math.random()*2000 -1000);

    const lineWidthLineset = 4;
    const borderWidthBarset = 2;

    const options: TOptions = {
        responsive: true,
        interaction: {
            mode: 'index' as const,
            intersect: false,
            axis: "xy",
            includeInvisible: false
        },
        color: colorLegendText,
        stacked: false,
        plugins: {
            title: {
                display: true,
                text: textChartTitle,
                position: "top",
                color: colorChartTitle,
                align: "start",
                padding: 10,
                fullSize: true,
                font: {}
            },
            // colors: {},
            // filler: {},
            // legend: {
            //     title: {
            //         color: "#00FF00"
            //     }
            // },
            // tooltip: {},
            // subtitle: {},
            // decimation: [],
        },
        scales: {
            x: {
                //     display: true,
                axis: "x",
                type: 'category' as const,
                position: 'bottom' as const,
                title: {
                    text: labelAxisX,
                    display: true,
                    align: "start",
                    color: colorAxisLabelX,
                    padding: 0,
                    font: {
                        // size: 20
                    }
                },
                grid: {
                    display: true,
                    color: colorGrid
                },
                ticks: {
                    color: colorTickLabels,
                },
                border: {
                    color: colorAxisLine
                }
            },
            yAxisLineset: {
                display: true,
                axis: "y",
                type: 'linear' as const,
                position: 'left' as const,
                title: {
                    text: labelAxisLineset,
                    display: true,
                    align: "end",
                    color: colorAxisLabelLineset,
                    padding: 0,
                    font: {
                        // size: 20
                    }
                },
                grid: {
                    display: true,
                    color: colorGrid,
                },
                ticks: {
                    color: colorTickLabels
                },
                border: {
                    color: colorAxisLine
                }
            },
            yAxisBarset: {
                display: true,
                axis: "y",
                type: 'linear' as const,
                position: 'right' as const,
                grid: {
                    drawOnChartArea: false,
                    color: colorGrid,
                },
                title: {
                    text: labelAxisBarset,
                    display: true,
                    align: "end",
                    color: colorAxisLabelBarset,
                    padding: 0,
                    font: {}
                },
                ticks: {
                    color: colorTickLabels
                },
                border: {
                    color: colorAxisLine
                }
            },
        },
    };

    const data: ChartData<TType, number[], string> = {
        labels: labels,
        datasets: [
            {
                type: 'line' as const,
                label: labelLineset,
                data: dataLineset,
                borderColor: colorLinesetLine,
                backgroundColor: colorLinesetFill,
                borderWidth: lineWidthLineset,
                fill: false,
                yAxisID: 'yAxisLineset',
            },
            {
                type: 'bar' as const,
                label: labelBarset,
                data: dataBarset,
                borderColor: colorBarsetOutline,
                backgroundColor: colorBarsetFill,
                borderWidth: borderWidthBarset,
                yAxisID: 'yAxisBarset',
            },
        ],
    };

    return (
        <Chart type='bar' data={data} options={options}/>
    )
}