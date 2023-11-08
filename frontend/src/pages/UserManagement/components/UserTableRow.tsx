import {StoredUserInfo} from "../../../models/UserManagementTypes.tsx";

type Props = {
    user?: StoredUserInfo
}

export default function UserTableRow( props:Readonly<Props> ) {

    function getReplacementIfNeeded(str: string, replacement: string) {
        if (!str || str.trim()==="")
            return replacement;
        return str;
    }

    if (!props.user)
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

    return (
        <tr>
            <td className={"Name"}>
                {props.user.avatar_url && <img className={"AvatarImage"} alt={"Avatar of user with ID "+props.user.id} src={props.user.avatar_url}/>}
                {"   "}
                {getReplacementIfNeeded(props.user.name, "------")}
            </td>
            <td>{props.user.role          }</td>
            <td className={"DenialReason"}>{props.user.denialReason  }</td>
            <td>{props.user.login         }</td>
            <td>{props.user.location      }</td>
            <td><a href={props.user.url}>{props.user.url}</a> </td>
            <td>{props.user.id            }</td>
            <td>{props.user.registrationId}</td>
            <td>{props.user.originalId    }</td>
        </tr>
    );
}