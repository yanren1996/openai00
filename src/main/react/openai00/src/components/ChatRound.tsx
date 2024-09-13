import { useEffect, useState } from "react";

interface ChatRoundProp {
  question: string,
  isMetadata: boolean,
  isStream: boolean
}

export default function ChatRound({ question, isMetadata, isStream }: ChatRoundProp) {
  const [answer, setAnswer] = useState('');
  useEffect(() => {
    let ignore = false;
    const call = async () => {
      if (!isMetadata) {
        const ajax = await fetch(`http://localhost:8080/ai/chat-model?message=${question}`);
        const text = await ajax.text();
        if (!ignore) {
          setAnswer(text);
        }
      } else {
        const ajax = await fetch(`http://localhost:8080/ai/chat-model/metadata?message=${question}`);
        const json = await ajax.json();
        const text = JSON.stringify(json, null, 2);
        if (!ignore) {
          setAnswer(text);
        }
      }
    }

    if (!isStream) { // 片段回復
      call();
      return () => { ignore = true; }
    } else { // 逐字回復
      const sse = new EventSource(`http://localhost:8080/ai/chat-model/stream?message=${question}`);
      sse.addEventListener('message', msg => {
        if (msg.data !== '%end%') {
          setAnswer(prevAns => prevAns + msg.data);
        } else {
          sse.close();
          ignore = true;
        }
      });
      return () => {
        if (!ignore) {
          sse.close();
        }
      }
    }
  }, [question, isMetadata, isStream]);
  
  return (<>
    <p>{question}</p>
    <pre>{answer}</pre>
  </>);
}