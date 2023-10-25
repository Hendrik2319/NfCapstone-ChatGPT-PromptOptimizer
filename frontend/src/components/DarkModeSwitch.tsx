import {ChangeEvent, useCallback, useEffect, useState} from "react";
import {
    DarkModeState,
    DarkModeSwitchState,
    getStoredDarkModeSwitchState,
    getSystemDarkModeState,
    setStoredDarkModeSwitchState
} from "./DarkModeSwitch.Functions.tsx";

type Props = {
    onChange: ( state: DarkModeState ) => void
}

export default function DarkModeSwitch( props: Readonly<Props> ) {
    const [ switchState, setSwitchState ] = useState<DarkModeSwitchState>("system");
    const { onChange: globalOnChangeState } = props;
    console.debug(`Rendering DarkModeSwitch { switchState:${switchState} }`);

    useEffect(() => {
        setSwitchState( getStoredDarkModeSwitchState() );
    }, []);

    const onChangeBySystem = useCallback((newState: DarkModeState) => {
        if (switchState === "system")
            globalOnChangeState(newState);
    }, [ globalOnChangeState, switchState ]);

    useEffect(() => {
        if (window.matchMedia) {
            const mediaQueryList = window.matchMedia('(prefers-color-scheme: dark)');
            const listener = (event:  MediaQueryListEvent) => onChangeBySystem(event.matches ? "dark" : "light");
            if (mediaQueryList.addEventListener) {
                mediaQueryList.addEventListener('change', listener)
                return () => mediaQueryList.removeEventListener('change', listener);
            }
            if (mediaQueryList.addListener) { // as fallback
                mediaQueryList.addListener(listener)
                return () => mediaQueryList.removeListener(listener);
            }
        }
    }, [ onChangeBySystem ]);

    function setSwitchAndStoredSwitchState(newSwitchState: DarkModeSwitchState) {
        setSwitchState(newSwitchState);
        setStoredDarkModeSwitchState(newSwitchState);
    }

    function onChangeBySwitch( event: ChangeEvent<HTMLSelectElement> ) {
        const newSwitchState = event.target.value;
        switch (newSwitchState) {
            case "system":
                props.onChange( getSystemDarkModeState() );
                setSwitchAndStoredSwitchState(newSwitchState);
                break;
            case "light":
            case "dark":
                props.onChange( newSwitchState );
                setSwitchAndStoredSwitchState(newSwitchState);
                break;
        }
    }

    return (
        <select className={"DarkModeSwitch"} onChange={onChangeBySwitch} value={switchState}>
            <option value={"system"}>System Theme</option>
            <option value={"light"}>Light Theme</option>
            <option value={"dark"}>Dark Theme</option>
        </select>
    )
}