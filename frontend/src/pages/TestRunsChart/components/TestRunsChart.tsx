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

export default function TestRunsChart() {
    const [ appTheme, setAppTheme ] = useState<DarkModeState>( getCurrentDarkModeState() )

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
    const colorAxisLabel = "#e87500";
    const colorLegendText = select( "#d0d0d0", "#000000");
    const colorGrid = select( "#606060", "#a0a0a0");
    const colorAxisLine = select( "#d0d0d0", "#000000");
    const colorLinesetLine = '#e87500';
    const colorLinesetFill = '#e87500';
    const colorBarsetOutline = select( "#d0d0d0", "#000000");
    const colorBarsetFill = select( "#80b0FF", "#5080FF");

    const textChartTitle = 'Chart.js Line Chart - Multi Axis';
    const labelLineset = 'Dataset 1';
    const labelBarset = 'Dataset 2';
    const labelAxisX = "x-axis";
    const labelAxisYLeft = "y-axis";
    const textLabelAxisYRight = "y1-axis";

    const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

    const dataLineset = labels.map(() => Math.random()*20 -10);
    const dataBarset = labels.map(() => Math.random()*2000 -1000);

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
                    color: colorAxisLabel,
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
            yAxisLeft: {
                display: true,
                axis: "y",
                type: 'linear' as const,
                position: 'left' as const,
                title: {
                    text: labelAxisYLeft,
                    display: true,
                    align: "end",
                    color: colorAxisLabel,
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
            yAxisRight: {
                display: true,
                axis: "y",
                type: 'linear' as const,
                position: 'right' as const,
                grid: {
                    drawOnChartArea: false,
                    color: colorGrid,
                },
                title: {
                    text: textLabelAxisYRight,
                    display: true,
                    align: "end",
                    color: colorAxisLabel,
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
                fill: true,
                yAxisID: 'yAxisLeft',
            },
            {
                type: 'bar' as const,
                label: labelBarset,
                data: dataBarset,
                borderColor: colorBarsetOutline,
                backgroundColor: colorBarsetFill,
                borderWidth: borderWidthBarset,
                yAxisID: 'yAxisRight',
            },
        ],
    };

    return (
        <Chart type='bar' data={data} options={options}/>
    )
}