import {UserInfo} from "../../models/UserManagementTypes.tsx";
import ScenariosPage from "../Scenarios/ScenariosPage.tsx";
import WaitUntilBecomeUserPage from "./components/WaitUntilBecomeUserPage.tsx";
import {SimpleCard} from "../../components/StandardStyledComponents.tsx";

type Props = {
    user?: UserInfo
    login: ()=>void
    logout: ()=>void
}

export default function MainPage( props: Readonly<Props> ) {

    if (!props.user?.isAuthenticated)
        return <SimpleCard>Please <button onClick={props.login}>Login</button></SimpleCard>

    if (!props.user.isUser && !props.user.isAdmin)
        return <WaitUntilBecomeUserPage logout={props.logout}/>

    return <ScenariosPage user={props.user}/>
}