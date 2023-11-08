import "./UserManagementPage.css"
import {useEffect, useState} from "react";
import {BackendAPI} from "../../global_functions/BackendAPI.tsx";
import {StoredUserInfo} from "../../models/UserManagementTypes.tsx";
import BreadCrumbs from "../../components/BreadCrumbs.tsx";
import styled from "styled-components";

const TableCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 0.5em;
  padding: 2em;
  background: var(--background-color);
  overflow-x: scroll;
`;

export default function UserManagementPage() {
    const [ users, setUsers ] = useState<StoredUserInfo[]>([]);

    useEffect(() => {
        BackendAPI.getAllStoredUsers(
            "UserManagementPage.useEffect",
            setUsers
        );
    }, []);

    function getReplacementIfNeeded(str: string, replacement: string) {
        if (!str || str.trim()==="")
            return replacement;
        return str;
    }

    return (
        <>
            <h3>User Management</h3>
            <BreadCrumbs/>
            <TableCard>
                <table className={"UserTable"}>
                    <thead>
                        <tr>
                            <th>User           </th>
                            <th>Role           </th>
                            <th>Deny Access    </th>
                            <th>Login Name     </th>
                            <th>Location       </th>
                            <th>URL            </th>
                            <th>ID             </th>
                            <th>Registration ID</th>
                            <th>Original ID    </th>
                        </tr>
                    </thead>
                    <tbody>
                    {
                        users.map( user => {
                            return (
                                <tr key={user.id}>
                                    <td className={"Name"}>
                                        {user.avatar_url && <img className={"AvatarImage"} alt={"Avatar of user with ID "+user.id} src={user.avatar_url}/>}
                                        {"   "}
                                        {getReplacementIfNeeded(user.name, "------")}
                                    </td>
                                    <td>{user.role          }</td>
                                    <td className={"DenialReason"}>{user.denialReason  }</td>
                                    <td>{user.login         }</td>
                                    <td>{user.location      }</td>
                                    <td><a href={user.url}>{user.url}</a> </td>
                                    <td>{user.id            }</td>
                                    <td>{user.registrationId}</td>
                                    <td>{user.originalId    }</td>
                                </tr>
                            )
                        })
                    }
                    </tbody>
                </table>
            </TableCard>
        </>
    )
}