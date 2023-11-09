import {BackendAPI} from "../../../global_functions/BackendAPI.tsx";
import {useEffect, useState} from "react";
import {SimpleCard} from "../../../components/StandardStyledComponents.tsx";

type Props = {
    logout: ()=>void
}

export default function WaitUntilBecomeUserPage( props: Readonly<Props> ) {
    const [ denialReason, setDenialReason ] = useState<string>("");

    useEffect(() => {
        BackendAPI.getDenialReasonForCurrentUser(
            "WaitUntilBecomeUserPage.useEffect",
            setDenialReason
        );
    }, []);

    return (
        <SimpleCard>
            {
                denialReason === ""
                    ?
                    <p>
                        You are now logged in, but should wait
                        until an administrator grants you access to the app.
                    </p>
                    :
                    <>
                        <p>
                            You are now logged in, but an administrator has decided
                            to deny you access to the app for the following reason:
                        </p>
                        <p>
                            {denialReason}
                        </p>
                    </>
            }
            <button onClick={props.logout}>Logout</button>
        </SimpleCard>
    )
}