import './SimpleChatPage.css';
import {ChangeEvent, FormEvent, useState} from "react";
import {Answer, DEBUG, Prompt, SHOW_RENDERING_HINTS} from "../../models/BaseTypes.tsx";
import {askChatGPT} from "../../global_functions/BackendAPI.tsx";

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
        askChatGPT(
            prompt,
            "SimpleChatView.onSubmitForm",
            answer => {
                if (DEBUG) console.debug(`SimpleChatView: answer ->`, answer);
                setAnswer( answer );
            }
        );
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