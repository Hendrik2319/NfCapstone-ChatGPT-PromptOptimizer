import {Role, UserTableRowProps} from "../../../models/UserManagementTypes.tsx";
import {SVGsInVars} from "../../../assets/SVGsInVars.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import {trimLongText} from "../../../global_functions/Tools.tsx";
import {ButtonSVG} from "../../../components/StandardStyledComponents.tsx";
import styled from "styled-components";

const EditButton = styled.span`
  display: inline;
  background: none;
  border: none;
  color: var(--text-color);
`;

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
        saveUser,
        showEditReasonDialog
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
        saveUser({
            ...user,
            role: event.target.value as Role
        });
    }

    function onDenialReasonCheckboxChange( event: ChangeEvent<HTMLInputElement> ) {
        if (event.target.checked)
            showEditReasonDialog({user});
        else
            saveUser({...user, denialReason: ""});
    }

    function getRoleLabel(role: Role) {
        switch (role) {
            case "ADMIN": return "Admin";
            case "USER": return "User";
            case "UNKNOWN_ACCOUNT": return "New Account";
        }
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
                    <EditButton onClick={onSetEditRoleActive}>
                        {getRoleLabel(user.role)+" "}
                        <ButtonSVG>{SVGsInVars.Edit}</ButtonSVG>
                    </EditButton>
                }
                {
                    editRoleActive &&
                    <select value={user.role} onChange={onChangeRole}>
                        <option value={"ADMIN"          }>{getRoleLabel("ADMIN"          )}</option>
                        <option value={"USER"           }>{getRoleLabel("USER"           )}</option>
                        <option value={"UNKNOWN_ACCOUNT"}>{getRoleLabel("UNKNOWN_ACCOUNT")}</option>
                    </select>
                }
            </td>

            <td className={"DenialReason"}>
                <input type={"checkbox"} checked={!(!user.denialReason)} onChange={onDenialReasonCheckboxChange}/>
                {
                    user.denialReason &&
                    <EditButton onClick={() => showEditReasonDialog({ user })}>
                        { trimLongText( user.denialReason, 35 )+" " }
                        <ButtonSVG>{ SVGsInVars.Edit }</ButtonSVG>
                    </EditButton>
                }
            </td>

            <td>{user.login         }</td>
            <td>{user.location      }</td>
            <td><a href={user.url} target={"_blank"}>{user.url}</a> </td>
            <td>{user.id            }</td>
            <td>{user.registrationId}</td>
            <td>{user.originalId    }</td>
        </tr>
    );
}