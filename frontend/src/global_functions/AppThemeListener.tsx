import {DarkModeState} from "../pages/Main/components/DarkModeSwitch.Functions.tsx";

export type AppThemeListener = (state: DarkModeState) => void;

const listOfAppThemeListener: AppThemeListener[] = [];

export function addAppThemeListener( listener: AppThemeListener ) {
    listOfAppThemeListener.push(listener);
}

export function removeAppThemeListener( listener: AppThemeListener ) {
    const pos = listOfAppThemeListener.indexOf(listener);
    if (pos>=0)
        listOfAppThemeListener.splice(pos, 1);
}

export function notifyAppThemeListener( state: DarkModeState ) {
    for (const listener of listOfAppThemeListener)
        listener(state);
}


