import {EditDenialReasonDialogOptions, StoredUserInfo} from "../../../models/UserManagementTypes.tsx";
import {ChangeEvent, FormEvent, useState} from "react";
import {SHOW_RENDERING_HINTS} from "../../../models/BaseTypes.tsx";
import styled from "styled-components";

const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  
  & > div {
    text-align: right;
  }
  & > * {
    margin: 0.5em 0;
  }
  & > *:first-child { margin-top: 0; }
  & > *:last-child  { margin-bottom: 0; }
`;

type Props = {
    saveChanges: (changedUser: StoredUserInfo) => void
    closeDialog: ()=>void
    setInitFunction: ( initFunction: (options: EditDenialReasonDialogOptions)=> void ) => void
}

export default function EditDenialReasonDialog( props: Readonly<Props> ) {
    const [ reason, setReason ] = useState<string>("");
    const [ user, setUser ] = useState<StoredUserInfo>();
    if (SHOW_RENDERING_HINTS) console.debug("Rendering EditDenialReasonDialog");

    props.setInitFunction((options: EditDenialReasonDialogOptions) => {
        setReason( !options.user.denialReason ? "" : options.user.denialReason );
        setUser( options.user );
    });

    function onChange( event: ChangeEvent<HTMLTextAreaElement> ) {
        setReason( event.target.value );
    }

    function onSubmit( event: FormEvent<HTMLFormElement> ) {
        event.preventDefault();
        if (user) {
            props.saveChanges({...user, denialReason: reason});
            props.closeDialog();
        }
    }

    return (
        <StyledForm onSubmit={onSubmit}>
            <label>Enter a reason for denial of the user:</label>
            <textarea
                value={reason}
                onChange={onChange}
                rows={10}
                cols={40}
            />
            <div>
                <button>Set</button>
                <button type="button" onClick={props.closeDialog}>Cancel</button>
            </div>
        </StyledForm>
    )
}