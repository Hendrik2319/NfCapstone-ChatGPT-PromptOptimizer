import "./ApiStateIndicator.css"
import axios from "axios";
import {useEffect, useState} from "react";
import {ApiState} from "../Types.tsx";

export default function ApiStateIndicator() {
    const [ state, setState ] = useState<ApiState>()
    console.debug(`Rendering ApiStateIndicator { state:${state} }`);

    useEffect( getState, [] )

    function getState() {
        axios
            .get("/api/apistate")
            .then(response => {
                console.debug(`ApiState: getState() ->`, response.data);
                setState(response.data)
            })
            .catch(reason => {
                console.error("Error in ApiState.getState", reason);
            });
    }

    const isEnabled = state?.enabled;
    return (
        <div className={ "ApiStateIndicator " + (isEnabled ? "enabled" : "disabled") }>

            API:
            <span>
                { isEnabled ? "Enabled" : "Disabled" }
            </span>

        </div>
    )
}