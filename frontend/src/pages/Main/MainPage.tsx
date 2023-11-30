import {UserInfo} from "../../models/UserManagementTypes.tsx";
import ScenariosPage from "../Scenarios/ScenariosPage.tsx";
import WaitUntilBecomeUserPage from "./components/WaitUntilBecomeUserPage.tsx";
import {MainCard} from "../../components/StandardStyledComponents.tsx";

type Props = {
    user?: UserInfo
    logins: {
        github: ()=>void,
        google: ()=>void,
    }
    logout: ()=>void
}

export default function MainPage( props: Readonly<Props> ) {

    if (!props.user?.isAuthenticated)
        return <MainCard>
            {"Please "}
            <button onClick={props.logins.github}>Login with GitHub</button>
            {" or "}
            <button onClick={props.logins.google}>Login with Google</button>
        </MainCard>

    if (!props.user.isUser && !props.user.isAdmin)
        return <WaitUntilBecomeUserPage logout={props.logout}/>

    return <ScenariosPage user={props.user}/>
}