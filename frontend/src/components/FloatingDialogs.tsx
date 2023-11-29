import './FloatingDialogs.css'
import {ReactNode, useState, MouseEvent as ReactMouseEvent} from "react";
import {SHOW_RENDERING_HINTS} from "../models/BaseTypes.tsx";

export type DialogControl<DialogOptions> = {
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options:DialogOptions)=> void ) => void
}

export function createDialog<DialogOptions>(
    id:string,
    writeContent: ( dialogControl: DialogControl<DialogOptions> ) => ReactNode,
    disableCancelWithBackgroundClick?: boolean
) {

    let initFunction: undefined | ((options:DialogOptions)=> void) = undefined;
    let showDialogFunction: undefined | ((showDialog: boolean) => void) = undefined;

    function showDialog( visible:boolean, options?:DialogOptions ) {
        if (SHOW_RENDERING_HINTS) console.debug(`FloatingDialog[ ${id} ] -> showDialog`, { visible, options })
        if (showDialogFunction) {
            showDialogFunction(visible);
            if (visible && initFunction && options)
                initFunction( options );
        } else
            console.error(`Error in FloatingDialog[ ${id} ]: showDialogFunction isn't set`);
    }

    function closeDialog() {
        showDialog(false)
    }

    function setInitFunction(initFunction_: (options:DialogOptions)=> void ) {
        initFunction = initFunction_;
    }

    function setShowDialogFunction(showDialogFunction_: (showDialog: boolean) => void): void {
        showDialogFunction = showDialogFunction_;
    }

    return {
        showDialog : (options?:DialogOptions) => showDialog(true, options),
        closeDialog,
        writeHTML  : () =>
            <Dialog
                getContent={() => writeContent({ closeDialog, setInitFunction })}
                closeDialog={closeDialog}
                setShowDialogFunction={setShowDialogFunction}
                cancelWithBackgroundClick={!disableCancelWithBackgroundClick}
            />,
    }
}


type Props = {
    getContent: () => ReactNode,
    closeDialog: () => void,
    setShowDialogFunction: ( showDialogFunction: ( showDialog: boolean ) => void ) => void,
    cancelWithBackgroundClick: boolean,
}

function Dialog( props: Readonly<Props> ): ReactNode {
    const [visible, setVisible] = useState<boolean>(false);

    props.setShowDialogFunction(setVisible);

    function onBackgroundClick(): void {
        if (props.cancelWithBackgroundClick)
            props.closeDialog();
    }

    function onDialogClick(event: ReactMouseEvent<HTMLDivElement, MouseEvent>): void {
        event.stopPropagation();
    }

    return (
        <div className={"FloatingDialogBackground" + (visible ? " visible" : "")} onClick={onBackgroundClick}>
            <div className="FloatingDialog" onClick={onDialogClick}>
                {props.getContent()}
            </div>
        </div>
    );
}