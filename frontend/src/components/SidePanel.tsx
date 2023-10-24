import "./SidePanel.css"
import {ReactNode, useState} from "react";

type Props = {
    children: ReactNode[]
};

export default function SidePanel( props:Props ) {
    const [ isOpen, setOpen ] = useState<boolean>(false);

    function toggleState() {
        setOpen( !isOpen );
    }

    return (
        <div className={"SidePanel"}>
            <button onClick={toggleState}>{isOpen ? ">" : "<"}</button>
            {isOpen && <div className={"SidePanelContent"}>{props.children}</div>}
        </div>
    )
}