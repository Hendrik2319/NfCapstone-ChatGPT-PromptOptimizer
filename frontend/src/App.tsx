import './App.css'
import SimpleChatView from "./components/SimpleChatView.tsx";
import ApiStateIndicator from "./components/ApiStateIndicator.tsx";

export default function App() {

    return (
        <>
            <h1>ChatGPT PromptOptimizer</h1>
            <ApiStateIndicator/>
            <SimpleChatView/>
        </>
    )
}

