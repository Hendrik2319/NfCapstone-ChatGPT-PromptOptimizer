import './App.css'
import SimpleChatView from "./components/SimpleChatView.tsx";
import ApiStateIndicator from "./components/mainpage/ApiStateIndicator.tsx";
import DarkModeSwitch from "./components/mainpage/DarkModeSwitch.tsx";
import {useEffect, useState} from "react";
import {DarkModeState, getCurrentDarkModeState} from "./components/mainpage/DarkModeSwitch.Functions.tsx";
import axios from "axios";
import SidePanel from "./components/mainpage/SidePanel.tsx";
import {DEBUG, UserInfos} from "./Types.tsx";
import {Link, Route, Routes} from "react-router-dom";
import MainPage from "./components/mainpage/MainPage.tsx";
import RouteProtection from "./components/mainpage/RouteProtection.tsx";
import TestRunsView from "./components/testrun/TestRunsView.tsx";

export default function App() {
    const [user, setUser] = useState<UserInfos>();
    if (DEBUG) console.debug("Rendering App {}");

    useEffect(() => {
        setAppTheme( getCurrentDarkModeState() );
    }, []);
    useEffect(determineCurrentUser, []);

    function setAppTheme(state: DarkModeState) {
        document.body.classList.remove( state === "dark" ? "light" : "dark" );
        document.body.classList.add(state);
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
                console.error("ERROR[TestRunsView.loadTestRuns]", error)
            })
    }

    function determineCurrentUser() {
        axios.get("/api/users/me")
            .then(response => {
                if (DEBUG) console.log(response.data);
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
            {
                user?.isAuthenticated && (user.isUser || user.isAdmin) &&
                <nav>
                    <Link to={"/"    }>Home</Link>
                    <Link to={"/chat"}>Simple Chat View</Link>
                </nav>
            }
            <Routes>
                <Route path={"/"} element={<MainPage user={user} login={login}/>}/>
                <Route element={<RouteProtection backPath="/" condition={user?.isAuthenticated && (user.isUser || user.isAdmin)}/>}>
                    <Route path={"/chat"} element={<SimpleChatView/>}/>
                    <Route path={"/scenario/:id"} element={<TestRunsView user={user}/>}/>
                </Route>
            </Routes>
        </>
    )
}

