import './App.css'
import SimpleChatView from "./components/SimpleChatView.tsx";
import ApiStateIndicator from "./components/ApiStateIndicator.tsx";
import DarkModeSwitch from "./components/DarkModeSwitch.tsx";
import {useEffect, useState} from "react";
import {DarkModeState, getCurrentDarkModeState} from "./components/DarkModeSwitch.Functions.tsx";

export default function App() {
    const [ darkModeState, setDarkModeState ] = useState<DarkModeState>("light")

    useEffect(() => {
        setDarkModeState( getCurrentDarkModeState() );
    }, []);

    return (
        <>
            <DarkModeSwitch onChange={setDarkModeState}/>
            { darkModeState }
            <h1>ChatGPT PromptOptimizer</h1>
            <ApiStateIndicator/>
            <SimpleChatView/>
        </>
    )
}

