export type DarkModeSwitchState = "dark" | "light" | "system"
export type DarkModeState = "dark" | "light"

const KEY_DARKMODE_SWITCH_STATE: string = "CurrentDarkModeSwitchState";

export function getSystemDarkModeState() {
    if (window.matchMedia) {
        const mediaQueryList = window.matchMedia('(prefers-color-scheme: dark)');
        return mediaQueryList.matches ? "dark" : "light";
    }

    return "light";
}

export function setStoredDarkModeSwitchState( newState: DarkModeSwitchState ) {
    localStorage.setItem(KEY_DARKMODE_SWITCH_STATE, newState);
}

export function getStoredDarkModeSwitchState(): DarkModeSwitchState {
    const currentState = localStorage.getItem(KEY_DARKMODE_SWITCH_STATE); // : DarkModeSwitchState
    if (currentState === "dark" || currentState === "light")
        return currentState;
    return "system";
}

export function getCurrentDarkModeState(): DarkModeState {
    const currentState = getStoredDarkModeSwitchState();
    if (currentState === "system")
        return getSystemDarkModeState();
    return currentState;
}
