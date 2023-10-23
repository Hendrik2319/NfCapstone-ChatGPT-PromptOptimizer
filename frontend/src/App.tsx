import './App.css'
import SimpleChatView from "./components/SimpleChatView.tsx";
import ApiStateIndicator from "./components/ApiStateIndicator.tsx";
import DarkModeSwitch from "./components/DarkModeSwitch.tsx";
import {useEffect, useState} from "react";
import {DarkModeState, getCurrentDarkModeState} from "./components/DarkModeSwitch.Functions.tsx";
import axios from "axios";

export default function App() {
    const [ darkModeState, setDarkModeState ] = useState<DarkModeState>("light")

    useEffect(() => {
        setAppTheme( getCurrentDarkModeState() );
    }, []);

    function setAppTheme(state: DarkModeState) {
        const bodies = document.getElementsByTagName("body");
        if (bodies.length>0) {
            bodies[0].classList.remove( state === "dark" ? "light" : "dark" );
            bodies[0].classList.add(state);
        }
        setDarkModeState( state );
    }

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080': window.location.origin;
        window.open(host + '/oauth2/authorization/github', '_blank');
    }

    function me() {
        axios.get("/api/users/me")
            .then(response => {
                console.log(response.data)
            })
    }

    return (
        <div className={darkModeState + " App"}>
            <div className={"TopRightBox"}>
                <button onClick={login}>Login</button>
                <button onClick={me}>me</button>
                <DarkModeSwitch onChange={setAppTheme}/>
                <ApiStateIndicator/>
            </div>
            <h1>ChatGPT PromptOptimizer</h1>
            <SimpleChatView/>
        </div>
    )
}

