import "./UserManagementPage.css"
import {useEffect, useState} from "react";
import {BackendAPI} from "../../global_functions/BackendAPI.tsx";
import {StoredUserInfo} from "../../models/UserManagementTypes.tsx";
import styled from "styled-components";
import UserTableRow from "./components/UserTableRow.tsx";

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

    return (
        <>
            <h3>User Management</h3>
            <TableCard>
                <table className={"UserTable"}>
                    <thead>
                        <UserTableRow/>
                    </thead>
                    <tbody>
                    {
                        users.map( user =>
                            <UserTableRow key={user.id} user={user}/>
                        )
                    }
                    </tbody>
                </table>
            </TableCard>
        </>
    )
}