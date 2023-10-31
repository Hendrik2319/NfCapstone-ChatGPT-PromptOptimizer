import "./SidePanel.css"
import {ReactNode, useState} from "react";
import {DEBUG} from "../../Types.tsx";

type Props = {
    children: ReactNode[]
};

export default function SidePanel( props: Readonly<Props> ) {
    const [isOpen, setIsOpen] = useState<boolean>(false);
    if (DEBUG) console.debug(`Rendering SidePanel { isOpen:${isOpen} }`);

    return (
        <div className={"SidePanel"}>
            <button onClick={()=>setIsOpen(!isOpen)}>{isOpen ? ">" : "<"}</button>
            <div className={"SidePanelContent"+(isOpen ? " open" : "")}>{props.children}</div>
        </div>
    )
}