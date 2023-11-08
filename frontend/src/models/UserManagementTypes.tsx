
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
