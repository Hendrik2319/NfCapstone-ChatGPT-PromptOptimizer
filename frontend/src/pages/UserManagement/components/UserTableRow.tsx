import {Role, UserTableRowProps} from "../../../models/UserManagementTypes.tsx";
import {SVGsInVars} from "../../../assets/SVGsInVars.tsx";
import {ChangeEvent, useEffect, useState} from "react";

type Props = {
    props?: UserTableRowProps
}

export default function UserTableRow( props_:Readonly<Props> ) {
    const [ editRoleActive, setEditRoleActive ] = useState<boolean>(false);

    useEffect(() => {
        if (!props_.props) return;
        props_.props.editRoleCtrl.registerMe(
            props_.props.user.id,
            () => setEditRoleActive(false)
        );
        return () => props_.props?.editRoleCtrl.unregisterMe(props_.props.user.id);
    }, [props_.props]);


    if (!props_.props) // if no props -> it's a header
        return <tr>
            <th>User           </th>
            <th>Role           </th>
            <th>Deny Access    </th>
            <th>Login Name     </th>
            <th>Location       </th>
            <th>URL            </th>
            <th>ID             </th>
            <th>Registration ID</th>
            <th>Original ID    </th>
        </tr>;

    const {
        user,
        editRoleCtrl,
        setRole
    } = props_.props;


    function getReplacementIfNeeded(str: string, replacement: string) {
        if (!str || str.trim()==="")
            return replacement;
        return str;
    }

    function onSetEditRoleActive() {
        setEditRoleActive(true);
        editRoleCtrl.setActive( user.id )
    }

    function onChangeRole( event: ChangeEvent<HTMLSelectElement> ) {
        setEditRoleActive(false);
        let selectedRole: Role = "USER";
        switch (event.target.value) {
            case "ADMIN"          : selectedRole = "ADMIN"          ; break;
            case "USER"           : break;
            case "UNKNOWN_ACCOUNT": selectedRole = "UNKNOWN_ACCOUNT"; break;
        }
        setRole(selectedRole);
    }

    return (
        <tr>
            <td className={"Name"}>
                {user.avatar_url && <img className={"AvatarImage"} alt={"Avatar of user with ID "+user.id} src={user.avatar_url}/>}
                {"   "}
                {getReplacementIfNeeded(user.name, "------")}
            </td>

            <td className={"NoWrap"}>
                {
                    !editRoleActive &&
                    <>
                        {user.role}
                        <button
                            className={"EditBtn"}
                            onClick={onSetEditRoleActive}
                        >
                            { SVGsInVars.Edit }
                        </button>
                    </>
                }
                {
                    editRoleActive &&
                    <select value={user.role} onChange={onChangeRole}>
                        <option value={"ADMIN"}>Admin</option>
                        <option value={"USER"}>User</option>
                        <option value={"UNKNOWN_ACCOUNT"}>Unknown Account</option>
                    </select>
                }
            </td>

            <td className={"DenialReason"}>
                {user.denialReason}
            </td>

            <td>{user.login         }</td>
            <td>{user.location      }</td>
            <td><a href={user.url}>{user.url}</a> </td>
            <td>{user.id            }</td>
            <td>{user.registrationId}</td>
            <td>{user.originalId    }</td>
        </tr>
    );
}