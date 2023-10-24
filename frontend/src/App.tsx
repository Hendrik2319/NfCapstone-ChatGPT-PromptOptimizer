import './App.css'
import SimpleChatView from "./components/SimpleChatView.tsx";
import ApiStateIndicator from "./components/ApiStateIndicator.tsx";
import DarkModeSwitch from "./components/DarkModeSwitch.tsx";
import {useEffect, useState} from "react";
import {DarkModeState, getCurrentDarkModeState} from "./components/DarkModeSwitch.Functions.tsx";
import axios from "axios";
import SidePanel from "./components/SidePanel.tsx";
import {UserInfos} from "./Types.tsx";

export default function App() {
    const [user, setUser] = useState<UserInfos>();
    console.debug("Rendering App {}");

    useEffect(() => {
        setAppTheme( getCurrentDarkModeState() );
    }, []);
    useEffect(determineCurrentUser, []);

    function setAppTheme(state: DarkModeState) {
        const bodies = document.getElementsByTagName("body");
        if (bodies.length>0) {
            bodies[0].classList.remove( state === "dark" ? "light" : "dark" );
            bodies[0].classList.add(state);
        }
    }

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080': window.location.origin;
        window.open(host + '/oauth2/authorization/github', '_self');
    }

    function logout() {
        axios.post("/api/logout")
            .then(() => {
                setUser(undefined)
            })
            .catch(error => {
                console.error(error)
            })
    }

    function determineCurrentUser() {
        axios.get("/api/users/me")
            .then(response => {
                console.log(response.data);
                setUser(response.data);
            })
    }

    return (
        <>
            <SidePanel>
                <ApiStateIndicator/>
                <DarkModeSwitch onChange={setAppTheme}/>
                <hr/>
                {!user?.isAuthenticated && <button onClick={login}>Login</button>}
                { user?.isAuthenticated && <button onClick={logout}>Logout</button>}
                <button onClick={determineCurrentUser}>me</button>
                {
                    user?.isAuthenticated &&
                    <div className={"CurrentUser"}>
                        Current user:<br/>
                        <a href={user.url} target="_blank">
                            {user.avatar_url && <img alt="user avatar image" src={user.avatar_url}/>}{" "}
                            {user.login}<br/>
                            {user.name}<br/>
                            [{user.id}]
                        </a>

                    </div>
                }
            </SidePanel>
            <h1>ChatGPT PromptOptimizer</h1>
            <SimpleChatView/>
        </>
    )
}

