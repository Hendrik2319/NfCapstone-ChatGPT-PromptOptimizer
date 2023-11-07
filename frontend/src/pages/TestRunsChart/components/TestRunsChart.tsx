import {useEffect, useState} from "react";
import {DarkModeState, getCurrentDarkModeState} from "../../Main/components/DarkModeSwitch.Functions.tsx";
import {addAppThemeListener, removeAppThemeListener} from "../../../global_functions/AppThemeListener.tsx";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {ChartDataSet} from "../../../models/TestRunsChartTypes.tsx";
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

type Props = {
    chartTitle: string

    axisXLabel: string
    xData: string[]

    lineSet?: ChartDataSet
    barSet?: ChartDataSet
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

    const colorText = select( "#d0d0d0", "#000000");
    const colorOrange = "#e87500";
    const colorBlue = select( "#80b0FF", "#5080FF");

    const colorChartTitle = colorText;
    const colorTickLabels = colorText;
    const colorAxisLabelLineSet = colorOrange;
    const colorAxisLabelBarSet = colorBlue;
    const colorAxisLabelX = colorText;
    const colorLegendText = colorText;
    const colorGrid = select( "#606060", "#a0a0a0");
    const colorAxisLine = select("#d0d0d0", "#000000");
    const colorLineSetLine = colorOrange;
    const colorLineSetFill = colorOrange;
    const colorBarSetOutline = select("#d0d0d0", "#000000");
    const colorBarSetFill = colorBlue;

    const lineWidthLineSet = 4;
    const borderWidthBarSet = 2;

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
                text: props.chartTitle,
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
                display: true,
                axis: "x",
                type: 'category' as const,
                position: 'bottom' as const,
                title: {
                    text: props.axisXLabel,
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
            yAxisLineSet: {
                display: !(!props.lineSet),
                axis: "y",
                type: 'linear' as const,
                position: 'left' as const,
                title: {
                    text: props.lineSet?.axisLabel ?? "Y-Axis (LineSet)",
                    display: true,
                    align: "end",
                    color: colorAxisLabelLineSet,
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
            yAxisBarSet: {
                display: !(!props.barSet),
                axis: "y",
                type: 'linear' as const,
                position: 'right' as const,
                grid: {
                    drawOnChartArea: false,
                    color: colorGrid,
                },
                title: {
                    text: props.barSet?.axisLabel ?? "Y-Axis (BarSet)",
                    display: true,
                    align: "end",
                    color: colorAxisLabelBarSet,
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
        labels: props.xData,
        datasets: [],
    };
    if (props.lineSet)
        data.datasets.push({
            type: 'line' as const,
            label: props.lineSet.label,
            data: props.lineSet.data,
            borderColor: colorLineSetLine,
            backgroundColor: colorLineSetFill,
            borderWidth: lineWidthLineSet,
            fill: false,
            yAxisID: 'yAxisLineSet',
        });
    if (props.barSet)
        data.datasets.push({
            type: 'bar' as const,
            label: props.barSet.label,
            data: props.barSet.data,
            borderColor: colorBarSetOutline,
            backgroundColor: colorBarSetFill,
            borderWidth: borderWidthBarSet,
            yAxisID: 'yAxisBarSet',
        });


    return (
        <Chart type='bar' data={data} options={options}/>
    )
}