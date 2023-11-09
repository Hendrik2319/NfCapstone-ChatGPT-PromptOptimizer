import './App.css'
import {useEffect, useState} from "react";
import {Navigate, Route, Routes, useLocation, useNavigate} from "react-router-dom";
import {DarkModeState, getCurrentDarkModeState} from "./pages/Main/components/DarkModeSwitch.Functions.tsx";
import {DEBUG, SHOW_RENDERING_HINTS} from "./models/BaseTypes.tsx";
import {UserInfo} from "./models/UserManagementTypes.tsx";
import RouteProtection from "./components/RouteProtection.tsx";
import ApiStateIndicator from "./pages/Main/components/ApiStateIndicator.tsx";
import DarkModeSwitch from "./pages/Main/components/DarkModeSwitch.tsx";
import SidePanel from "./pages/Main/components/SidePanel.tsx";
import MainPage from "./pages/Main/MainPage.tsx";
import SimpleChatPage from "./pages/SimpleChat/SimpleChatPage.tsx";
import TestRunsPage from "./pages/TestRuns/TestRunsPage.tsx";
import NewTestRunPage from "./pages/NewTestRun/NewTestRunPage.tsx";
import TestRunWaitPage from "./pages/TestRunWaitPage.tsx";
import {BackendAPI} from "./global_functions/BackendAPI.tsx";
import TestRunsChartPage from "./pages/TestRunsChart/TestRunsChartPage.tsx";
import {notifyAppThemeListener} from "./global_functions/AppThemeListener.tsx";
import UserManagementPage from "./pages/UserManagement/UserManagementPage.tsx";
import UserProfilePage from "./pages/UserProfilePage.tsx";

export default function App() {
    const [user, setUser] = useState<UserInfo>();
    const location = useLocation();
    const navigate = useNavigate();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering App");

    useEffect(() => {
        setAppTheme( getCurrentDarkModeState() );
    }, []);

    useEffect(determineCurrentUser, [ location.pathname ]);

    function setAppTheme(state: DarkModeState) {
        notifyAppThemeListener(state);
        document.body.classList.remove( state === "dark" ? "light" : "dark" );
        document.body.classList.add(state);
    }

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080': window.location.origin;
        window.open(host + '/oauth2/authorization/github', '_self');
    }

    function logout() {
        BackendAPI.logout("App.logout()", ()=>setUser(undefined));
    }

    function determineCurrentUser() {
        BackendAPI.determineCurrentUser(
            "App.determineCurrentUser()",
            user => {
                if (DEBUG) console.debug("determineCurrentUser", user);
                setUser(user);
            }
        )
    }

    return (
        <>
            <SidePanel>
                <ApiStateIndicator/>
                <DarkModeSwitch onChange={setAppTheme}/>
                <div className={"Spacer"}/>
                {!user?.isAuthenticated && <button onClick={login}>Login</button>}
                { user?.isAuthenticated && <button onClick={logout}>Logout</button>}
                <div className={"Spacer"}/>
                <button onClick={()=>navigate("/")}>Home</button>
                {
                    user?.isAuthenticated &&
                    <>
                        {
                            (user.isUser || user.isAdmin) &&
                            <button onClick={()=>navigate("/chat")}>Simple Chat View</button>
                        }
                        {
                            user.isAdmin &&
                            <button onClick={()=>navigate("/admin")}>User Management</button>
                        }
                        <button onClick={()=>navigate("/user")}>
                            { !user.login && "User Profile" }
                            {
                                user.login &&
                                <>
                                    {user.avatar_url && <img alt="user avatar image" src={user.avatar_url} className={"AvatarImage"}/>}
                                    {" "+user.login}
                                </>
                            }
                        </button>
                    </>
                }
            </SidePanel>
            <h1>ChatGPT PromptOptimizer</h1>
            <Routes>
                <Route path={"/"} element={<MainPage user={user} login={login} logout={logout}/>}/>
                <Route element={<RouteProtection backPath="/" condition={user?.isAuthenticated}/>}>
                    <Route path={"/user"} element={<UserProfilePage user={user!}/>}/>
                </Route>
                <Route element={<RouteProtection backPath="/" condition={user?.isAuthenticated && (user.isUser || user.isAdmin)}/>}>
                    <Route path={"/chat"} element={<SimpleChatPage/>}/>
                    <Route path={"/scenario/:id"} element={<TestRunsPage user={user}/>}/>
                    <Route path={"/scenario/:id/newtestrun"} element={<NewTestRunPage/>}/>
                    <Route path={"/scenario/:id/pleasewait"} element={<TestRunWaitPage/>}/>
                    <Route path={"/scenario/:id/chart"} element={<TestRunsChartPage/>}/>
                </Route>
                <Route element={<RouteProtection backPath="/" condition={user?.isAuthenticated && user.isAdmin}/>}>
                    <Route path={"/admin"} element={<UserManagementPage/>}/>
                </Route>
                <Route path={"/*"} element={<Navigate to={"/"}/>}/>
            </Routes>
        </>
    )
}

