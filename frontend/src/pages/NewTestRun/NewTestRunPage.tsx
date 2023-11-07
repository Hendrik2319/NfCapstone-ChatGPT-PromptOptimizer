import {TestCase, VariablesChangeMethod} from "../../models/TestRunTypes.tsx";
import styled from "styled-components";
import {SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";
import PromptEditAndView from "./components/PromptEditAndView.tsx";
import TestCasesEditAndView from "./components/TestCasesEditAndView.tsx";
import VariablesEdit from "./components/VariablesEdit.tsx";
import {loadCurrentNewTestRun, saveCurrentNewTestRun} from "../../global_functions/NewTestRunStoarage.tsx";
import {useNavigate, useParams} from "react-router-dom";
import {BackendAPI} from "../../global_functions/BackendAPI.tsx";
import BreadCrumbs from "../../components/BreadCrumbs.tsx";

export const BigLabel = styled.label`
  display: block;
  color: var(--text-color-biglabel);
  font-size: 1em;
  margin-top: 0.5em;
`;

const BigButton = styled.button`
  font-size: 1.2em;
  font-weight: bold;
  padding: 0.5em 2em;
`;

export default function NewTestRunPage() {
    const { id: scenarioId } = useParams();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering NewTestRunPanel", { scenarioId: scenarioId });
    let usedVars = new Set<number>();
    let variablesCompGetter: null | (()=>string[]) = null;
    let    promptCompGetter: null | (()=>string) = null;
    let testcasesCompGetter: null | (()=>TestCase[]) = null;
    let    promptVarChangeNotifier: null | VariablesChangeMethod = null;
    let testcasesVarChangeNotifier: null | VariablesChangeMethod = null;
    const navigate = useNavigate();

    if (!scenarioId) {
        navigate("/");
        return <>No Scenario found</>
    }

    let storedNewTestRun = loadCurrentNewTestRun(scenarioId) ?? {
        prompt: "",
        scenarioId,
        variables: [],
        testcases: []
    };

    function getVariables(): string[]   { return variablesCompGetter ? variablesCompGetter() : storedNewTestRun.variables; }
    function getPrompt   (): string     { return    promptCompGetter ?    promptCompGetter() : storedNewTestRun.prompt   ; }
    function getTestcases(): TestCase[] { return testcasesCompGetter ? testcasesCompGetter() : storedNewTestRun.testcases; }

    function saveFormValues(prompt: string, scenarioId: string, variables: string[], testcases: TestCase[]) {
        storedNewTestRun = {
            prompt,
            scenarioId,
            variables,
            testcases
        };
        saveCurrentNewTestRun(scenarioId, storedNewTestRun)
    }

    function performTestRun(scenarioId: string) {
        BackendAPI.performTestRun({
                prompt: getPrompt(),
                scenarioId: scenarioId,
                variables: getVariables(),
                testcases: getTestcases()
            },
            "NewTestRunPanel",
            () => navigate("/scenario/" + scenarioId));
        navigate("/scenario/"+ scenarioId +"/pleasewait")
    }

    function getVarColor(index: number): string {
        return "var(--text-background-var"+(index%6)+")";
    }

    function isAllowedToDeleteVar(varName: string): boolean {
        const testcases = getTestcases();
        const tc = testcases.find(testcase => {
            const values = testcase.get(varName);
            return values && values.length!==0;
        });
        if (tc) {
            alert("Can't delete variable.\r\nThere are at least 1 test case that have values for this variable.")
            return false;
        }
        return true;
    }

    const variablesChanged: VariablesChangeMethod = (index: number, oldVarName: string, newVarName: string): void => {
        if (promptVarChangeNotifier)
            promptVarChangeNotifier(index, oldVarName, newVarName);
        if (testcasesVarChangeNotifier)
            testcasesVarChangeNotifier(index, oldVarName, newVarName);
    }

    function onPromptChange( scenarioId: string, prompt: string ) {
        saveFormValues(prompt, scenarioId, storedNewTestRun.variables, storedNewTestRun.testcases)
    }

    function onVariablesChange( scenarioId: string, variables: string[] ) {
        saveFormValues(storedNewTestRun.prompt, scenarioId, variables, storedNewTestRun.testcases)
    }

    function onTestcasesChange( scenarioId: string, testcases: TestCase[] ) {
        saveFormValues(storedNewTestRun.prompt, scenarioId, storedNewTestRun.variables, testcases)
    }

    function cleanupTestcases(testcases: TestCase[], variables: string[]) {
        return testcases.map(testcase => {
            const cleanedTestcase: TestCase = new Map<string, string[]>();
            variables.forEach(varName => {
                const values = testcase.get(varName);
                cleanedTestcase.set(varName, !values ? [] : values.map(s => s));
            });
            return cleanedTestcase;
        });
    }

    return (
        <>
            <BreadCrumbs scenarioId={scenarioId} extraLabel={"New TestRun"}/>
            <BigLabel>Prompt :</BigLabel>
            <PromptEditAndView
                prompt={storedNewTestRun.prompt}
                getVariables={getVariables}
                getVarColor={getVarColor}
                updateUsedVars={usedVars_ => usedVars = usedVars_}
                onPromptChange={prompt => onPromptChange(scenarioId, prompt)}
                setGetter={fcn => promptCompGetter = fcn}
                setVarChangeNotifier={fcn => promptVarChangeNotifier = fcn}
            />
            <BigLabel>Variables :</BigLabel>
            <VariablesEdit
                variables={storedNewTestRun.variables}
                isAllowedToDelete={isAllowedToDeleteVar}
                getVarColor={getVarColor}
                notifyOthersAboutChange={variablesChanged}
                onVariablesChange={variables => onVariablesChange(scenarioId, variables)}
                setGetter={fcn => variablesCompGetter = fcn}
            />
            <BigLabel>Test Cases :</BigLabel>
            <TestCasesEditAndView
                testcases={cleanupTestcases(storedNewTestRun.testcases, storedNewTestRun.variables)}
                getVariables={getVariables}
                getUsedVars={() => usedVars}
                getVarColor={getVarColor}
                onTestcasesChange={testcases => onTestcasesChange(scenarioId, testcases)}
                setGetter={fcn => testcasesCompGetter = fcn}
                setVarChangeNotifier={fcn => testcasesVarChangeNotifier = fcn}
            />
            <BigButton onClick={()=>performTestRun(scenarioId)}>Start Test Run</BigButton>
        </>
    )
}
