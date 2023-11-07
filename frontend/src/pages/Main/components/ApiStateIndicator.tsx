import "./ApiStateIndicator.css"
import {useEffect, useState} from "react";
import {ApiState, DEBUG, SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import {BackendAPI} from "../../../global_functions/BackendAPI.tsx";

export default function ApiStateIndicator() {
    const [ state, setState ] = useState<ApiState>()
    if (SHOW_RENDERING_HINTS) console.debug("Rendering ApiStateIndicator", { state });

    useEffect( getState, [] )

    function getState() {
        BackendAPI.getApiState("ApiStateIndicator.getState", state => {
            if (DEBUG) console.debug(`ApiState: getState() ->`, state);
            setState(state)
        })
    }

    const isEnabled = state?.enabled;
    return (
        <div className={ "ApiStateIndicator " + (isEnabled ? "enabled" : "disabled") }>
            API: <span>{ isEnabled ? "Enabled" : "Disabled" }</span>
        </div>
    )
}