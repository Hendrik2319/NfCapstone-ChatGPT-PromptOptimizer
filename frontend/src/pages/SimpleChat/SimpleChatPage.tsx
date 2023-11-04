import './SimpleChatPage.css';
import {ChangeEvent, FormEvent, useState} from "react";
import axios from "axios";
import {Answer, DEBUG, Prompt, SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";

export default function SimpleChatPage() {
    const [ prompt, setPrompt ] = useState<Prompt>({ prompt:"" });
    const [ answer, setAnswer ] = useState<Answer>({ answer:"" });
    if (SHOW_RENDERING_HINTS) console.debug("Rendering SimpleChatView");

    function onInputChange( event: ChangeEvent<HTMLTextAreaElement> ) {
        setPrompt({
            prompt: event.target.value
        });
    }

    function onSubmitForm( event:FormEvent<HTMLFormElement> ) {
        event.preventDefault();
        if (DEBUG) console.debug(`SimpleChatView: prompt ->`, prompt);
        axios
            .post("/api/ask", prompt)
            .then(response => {
                if (DEBUG) console.debug(`SimpleChatView: answer ->`, response.data);
                setAnswer( response.data );
            })
            .catch(reason => {
                console.error("Error in SimpleChatView.onSubmitForm", reason);
            });
    }

    return (
        <form onSubmit={onSubmitForm} className={"SimpleChatForm"}>
            <label htmlFor="prompt">Prompt :</label>
            <textarea id="prompt" value={prompt.prompt} onChange={onInputChange} rows={10}/>
            <button>Send</button>
            <label htmlFor="answer">Answer :</label>
            <textarea id="answer" value={answer.answer} readOnly={true} rows={10}/>
        </form>
    )
}