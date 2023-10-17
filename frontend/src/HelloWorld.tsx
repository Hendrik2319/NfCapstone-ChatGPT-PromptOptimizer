import axios from "axios";
import {useEffect, useState} from "react";

export default function HelloWorld() {
    const [output, setOutput] = useState<string>("????")

    useEffect( getAll, [] )

    function getAll() {
        axios
            .get('/api/hello')
            .then(response => {
                if (response.status != 200)
                    throw new Error(`Got wrong status on load data: ${response.status}`);
                return response.data;
            })
            .then(data => {
                console.debug("HelloWorld -> data loaded", data);
                setOutput(data)
            })
            .catch(reason => {
                console.error(reason);
            })
    }

    return (
        <h4>Output: {output}</h4>
    )
}