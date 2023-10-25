import {UserInfos} from "../Types.tsx";
import ScenarioList from "./ScenarioList.tsx";

type Props = {
    user?: UserInfos
    login: ()=>void
}

export default function MainPage( props: Readonly<Props> ) {

    if (!props.user?.isAuthenticated)
        return <><br/>Please <button onClick={props.login}>Login</button></>

    if (!props.user.isUser && !props.user.isAdmin)
        return <><br/>You are now logged in, but should wait until an administrator grants you access to the app.</>

    return <ScenarioList/>
}