import {TestAnswer, TestCase, TestRun} from "./Types.tsx";

type Props = {
    testRun: TestRun
}

export default function TestRunCard( props:Readonly<Props> ) {

    function showVarValues(values: string[], varName: string) {
        return (
            <div>
                {varName}: {values.map( str=>"\""+str+"\"" ).join(", ")}
            </div>
        )
    }
    function showTestCase(testcase: TestCase) {
        const elements: JSX.Element[] = [];
        if (testcase.forEach) // TODO: choose another type for TestCase
            testcase.forEach( (values: string[], varName: string) =>
                elements.push( showVarValues(values, varName) )
            )
        else
            console.error("No testcase.forEach",testcase)
        return (
            <div>{elements}</div>
        )
    }

    function showAnswer(answer: TestAnswer) {
        return (
            <div>
                <div>[{answer.indexOfTestCase}] {answer.label}</div>
                <div>{answer.answer}</div>
            </div>
        )
    }

    return (
        <div className="TestRunCard">
            <div>id        : {props.testRun.id        }</div>
            <div>scenarioId: {props.testRun.scenarioId}</div>
            <div>timestamp : {props.testRun.timestamp }</div>
            <div>prompt    : {props.testRun.prompt    }</div>
            <div>variables : {props.testRun.variables }</div>
            <div>testcases : {props.testRun.testcases.map(showTestCase)}</div>
            <div>answers   : {props.testRun.answers  .map(showAnswer  )}</div>
        </div>
    )
}