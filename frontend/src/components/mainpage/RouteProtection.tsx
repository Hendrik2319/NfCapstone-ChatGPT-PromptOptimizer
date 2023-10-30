import {Navigate, Outlet} from "react-router-dom";

type Props = {
    condition?: boolean
    backPath: string
}

export default function RouteProtection(props: Readonly<Props> ) {
    return props.condition ? <Outlet /> : <Navigate to={props.backPath} />
}