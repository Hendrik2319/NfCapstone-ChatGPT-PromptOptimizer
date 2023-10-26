import "./SidePanel.css"
import {ReactNode} from "react";
import {DEBUG} from "../../Types.tsx";

type Props = {
    children: ReactNode[]
};

export default function SidePanel( props: Readonly<Props> ) {
    let isOpen: boolean = false;
    if (DEBUG) console.debug(`Rendering SidePanel { isOpen:${isOpen} }`);

    function toggleState() {
        isOpen = !isOpen;
        const panel = document.getElementById("SidePanelContent");
        const button = document.getElementById("SidePanelButton");
        if (panel) {
            if (isOpen)
                panel.classList.add("open");
            else
                panel.classList.remove("open");
        }
        if (button)
            button.textContent = isOpen ? ">" : "<"
    }

    return (
        <div className={"SidePanel"}>
            <button id="SidePanelButton" onClick={toggleState}>{isOpen ? ">" : "<"}</button>
            <div id="SidePanelContent" className={"SidePanelContent"+(isOpen ? " open" : "")}>{props.children}</div>
        </div>
    )
}