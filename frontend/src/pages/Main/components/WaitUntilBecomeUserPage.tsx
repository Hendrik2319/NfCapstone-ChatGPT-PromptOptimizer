import {BackendAPI} from "../../../global_functions/BackendAPI.tsx";
import {useEffect, useState} from "react";
import styled from "styled-components";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 0.5em;
  padding: 2em;
  background: var(--background-color);
`;

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
            <p>
                {
                    denialReason === ""
                        ?
                        <>
                            You are now logged in, but should wait
                            until an administrator grants you access to the app.
                        </>
                        :
                        <>
                            You are now logged in, but an administrator has decided
                            to deny you access to the app for the following reason:<br/>
                            {denialReason}
                        </>
                }

            </p>
            <button onClick={props.logout}>Logout</button>
        </SimpleCard>
    )
}