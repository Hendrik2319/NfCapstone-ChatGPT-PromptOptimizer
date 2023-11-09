import {UserInfo} from "../models/UserManagementTypes.tsx";
import styled from "styled-components";
import {BigLabel, Id, MainCard} from "../components/StandardStyledComponents.tsx";

const AvatarImage = styled.img`
  width: 10em;
  height: 10em;
`;

const BigLabel2 = styled(BigLabel)`
  width: 6em;
  display: inline-block;
  margin: 0;
  padding: 0;
`;

const MyMainCard = styled(MainCard)`
  & > * {
    margin: 0.5em;
  }
`;

const ValuesCard = styled.div`
  & > * {
    margin: 0.2em;
  }
`;

type Props = {
    user: UserInfo
}

export default function UserProfilePage( props:Readonly<Props> ) {

    function getRoleLabel() {
        if (props.user.isAdmin) return "Admin";
        if (props.user.isUser) return "User";
        return "New Account";
    }

    return (
        <>
            <h3>User Profile</h3>
            <MyMainCard className={"FlexRow"}>
                {
                    props.user.avatar_url &&
                    <div><AvatarImage alt={""} src={props.user.avatar_url}/></div>
                }
                <ValuesCard className={"FlexColumn"}>
                    <Id>id : {props.user.id}</Id>
                    <Id>database id : {props.user.userDbId}</Id>
                    <div><BigLabel2>Name    </BigLabel2>{props.user.name    }</div>
                    <div><BigLabel2>Role    </BigLabel2>{getRoleLabel()     }</div>
                    <div><BigLabel2>Login   </BigLabel2>{props.user.login   }</div>
                    <div><BigLabel2>Location</BigLabel2>{props.user.location}</div>
                    <div><BigLabel2>URL     </BigLabel2>
                        <a href={props.user.url} target={"_blank"}>{props.user.url}</a>
                    </div>
                </ValuesCard>
            </MyMainCard>
        </>
    )
}