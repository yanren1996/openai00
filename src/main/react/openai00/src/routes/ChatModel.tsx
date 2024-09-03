import { useRef, useState } from "react";

interface Message {
  id: string,
  content: string
}

export default function () {

  const [msg, setMsg] = useState<string>('');
  const [history, sethistory] = useState<Array<Message>>([]);
  const msgCount = useRef<number>(0);

  const handleClick = async () => {
    // 取出user訊息先印出
    const newMsg = msg;
    setMsg('');
    sethistory(pre => [...pre, { id: 'm_' + msgCount.current++, content: newMsg }]);
    // 發送伺服器&印出ai回應
    const ajax = await fetch('http://localhost:8080/ai/chat-model?message=' + newMsg);
    const text = await ajax.text();
    sethistory(pre => [...pre, { id: 'm_' + msgCount.current++, content: text }])
  }

  return (<>
    <input value={msg} onChange={e => setMsg(e.target.value)} />
    <button onClick={handleClick}>問ai</button>
    <div>
      {history.map(item => <p key={item.id}>{item.content}</p>)}
    </div>
  </>);
}