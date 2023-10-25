import {Navigate, Outlet} from "react-router-dom";

type Props = {
    isAuthenticated?: boolean
}

export default function ProtectedRoutes( props: Readonly<Props> ) {
    return(
        props.isAuthenticated ? <Outlet /> : <Navigate to="/" />
    )
}