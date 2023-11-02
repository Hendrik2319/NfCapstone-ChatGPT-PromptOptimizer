import "/test1.png"
import img2 from "/test2.png"
import img3 from "../../../public/test3.png"
import "/src/assets/test5.png"
import img6 from "/src/assets/test6.png"
import img7 from "../../assets/test7.png"
import styled from "styled-components";

const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 4px;
  padding: 0.2em;
  background: var(--background-color);
`;

export default function ImageTest() {
    return (
        <SimpleCard>
            <div><img alt="" src="/test.png"/>&lt;img src="/test.png"/&gt;</div>
            <div><img alt="" src="/test1.png"/>import "/test1.png" + &lt;img src="/test1.png"/&gt;</div>
            <div><img alt="" src={img2}/>import img2 from "/test2.png" + &lt;img src=(img2)/&gt;</div>
            <div><img alt="" src={img3}/>import img3 from "../../../public/test3.png" + &lt;img src=(img3)/&gt;</div>
            <div><img alt="" src="/src/assets/test4.png"/>&lt;img src="/src/assets/test4.png"/&gt;</div>
            <div><img alt="" src="/src/assets/test5.png"/>import "/src/assets/test5.png" + &lt;img src="/src/assets/test5.png"/&gt;</div>
            <div><img alt="" src={img6}/>import img6 from "/src/assets/test6.png" + &lt;img src=(img6)/&gt;</div>
            <div><img alt="" src={img7}/>import img7 from "../../assets/test7.png" + &lt;img src=(img7)/&gt;</div>
        </SimpleCard>
    )
}