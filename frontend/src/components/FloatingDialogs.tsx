import './FloatingDialogs.css'
import {ReactNode} from "react";
import {SHOW_RENDERING_HINTS} from "../Types.tsx";

export type DialogControl<DialogOptions> = {
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options:DialogOptions)=> void ) => void
}

export function createDialog<DialogOptions>( id:string, writeContent: ( dialogControl: DialogControl<DialogOptions> ) => ReactNode ) {

    let initFunction: undefined | ((options:DialogOptions)=> void) = undefined

    function showDialog( visible:boolean, options?:DialogOptions ) {
        if (SHOW_RENDERING_HINTS) console.debug(`FloatingDialog[ ${id} ] -> showDialog`, { visible, options })
        const dialog = document.querySelector('#'+id)
        if (dialog) {
            if (visible) {
                dialog.classList.add('visible')
                if (initFunction && options)
                    initFunction( options )
            }
            else
                dialog.classList.remove('visible')
        } else
            console.error(`Can't find FloatingDialog[ ${id} ]`)
    }

    function closeDialog() {
        showDialog(false)
    }

    function setInitFunction(initFunction_: (options:DialogOptions)=> void ) {
        initFunction = initFunction_;
    }

    return {
        showDialog : (options?:DialogOptions) => showDialog(true, options),
        closeDialog,
        writeHTML  : () => (
            <div id={id} className="FloatingDialogBackground">
                <div className="FloatingDialog">
                    {writeContent({ closeDialog, setInitFunction })}
                </div>
            </div>
        )
    }
}
