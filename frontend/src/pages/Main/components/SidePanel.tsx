import "./SidePanel.css"
import {ReactNode, useState} from "react";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";

type Props = {
    children: ReactNode[]
};

export default function SidePanel( props: Readonly<Props> ) {
    const [isOpen, setIsOpen] = useState<boolean>(false);
    if (SHOW_RENDERING_HINTS) console.debug("Rendering SidePanel", { isOpen });

    return (
        <div className={"SidePanel"}>
            <button className={"SidePanelOpenerBtn"} onClick={()=>setIsOpen(!isOpen)}>{isOpen ? ">" : "<"}</button>
            <div className={"SidePanelContent"+(isOpen ? " open" : "")}>{props.children}</div>
        </div>
    )
}