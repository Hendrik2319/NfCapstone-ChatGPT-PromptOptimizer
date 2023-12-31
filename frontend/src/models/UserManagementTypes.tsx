import {OnlyOneActiveController} from "../global_functions/OnlyOneActive.tsx";

export type UserInfo = {
    isAuthenticated : boolean
    isUser          : boolean
    isAdmin         : boolean
    id              : string
    userDbId        : string
    login           : string
    name            : string
    location        : string
    url             : string
    avatar_url      : string
};

export type StoredUserInfo = {
    id             : string
    role           : Role
    registrationId : string
    originalId     : string
    login          : string
    name           : string
    location       : string
    url            : string
    avatar_url     : string
    denialReason   : string
};

export type Role = "ADMIN" | "USER" | "UNKNOWN_ACCOUNT";

export type SaveUserFunction = ( changedUser: StoredUserInfo ) => void;
export type UserTableRowProps = {
    user: StoredUserInfo
    editRoleCtrl: OnlyOneActiveController
    saveUser: SaveUserFunction
    showEditReasonDialog: ( options: EditDenialReasonDialogOptions ) => void
};

export type EditDenialReasonDialogOptions = {
    user: StoredUserInfo
}