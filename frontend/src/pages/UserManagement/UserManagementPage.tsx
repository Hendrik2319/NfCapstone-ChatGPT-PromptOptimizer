import "./UserManagementPage.css"
import {useEffect, useState} from "react";
import {BackendAPI} from "../../global_functions/BackendAPI.tsx";
import {EditDenialReasonDialogOptions, StoredUserInfo} from "../../models/UserManagementTypes.tsx";
import styled from "styled-components";
import UserTableRow from "./components/UserTableRow.tsx";
import {createOnlyOneActiveController} from "../../global_functions/OnlyOneActive.tsx";
import {createDialog} from "../../components/FloatingDialogs.tsx";
import EditDenialReasonDialog from "./components/EditDenialReasonDialog.tsx";

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

    const editRoleCtrl = createOnlyOneActiveController();

    function saveChangedUser( changedUser: StoredUserInfo ) {
        BackendAPI.updateStoredUser(
            changedUser,
            "UserManagementPage.saveChangedUser",
            () => {
                BackendAPI.getAllStoredUsers(
                    "UserManagementPage.saveChangedUser",
                    setUsers
                );
            }
        )
    }

    const editDenialReasonDialog =
        createDialog<EditDenialReasonDialogOptions>(
            'EditDenialReasonDialog',
            dialogControl =>
                <EditDenialReasonDialog
                    saveChanges={saveChangedUser}
                    setInitFunction={dialogControl.setInitFunction}
                    closeDialog={dialogControl.closeDialog}
                />
        )

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
                            <UserTableRow key={user.id} props={{
                                user, editRoleCtrl,
                                saveUser: saveChangedUser,
                                showEditReasonDialog: editDenialReasonDialog.showDialog
                            }}/>
                        )
                    }
                    </tbody>
                </table>
            </TableCard>
            {editDenialReasonDialog.writeHTML()}
        </>
    )
}