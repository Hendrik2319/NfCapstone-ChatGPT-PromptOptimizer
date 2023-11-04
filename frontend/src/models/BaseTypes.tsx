export const SHOW_RENDERING_HINTS: boolean = true;
export const DEBUG: boolean = true;

export type Prompt = {
    prompt: string
}
export type Answer = {
    answer: string
}

export type ApiState = {
    enabled: boolean
}

export type UserInfos = {
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
}
