import { useRef, useState } from "react";

interface Message {
  id: string,
  content: string
}
interface Opt {
  isStream: boolean,
  isMetadata: boolean
}

export default function () {

  const [msg, setMsg] = useState<string>('');
  const [opt, setOpt] = useState<Opt>({ isStream: false, isMetadata: false });
  const [history, sethistory] = useState<Array<Message>>([]);
  const msgCount = useRef<number>(0);

  const handleOptChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setOpt(preOpt => ({ ...preOpt, [e.target.name]: e.target.checked }));
  }

  const handleClick = async () => {
    // 取出user訊息先印出
    const newMsg = msg;
    setMsg('');
    sethistory(pre => [...pre, { id: 'm_' + msgCount.current++, content: newMsg }]);
    // 發送伺服器&印出ai回應
    const ajax = await fetch(`http://localhost:8080/ai/chat-model${opt.isMetadata ? '/metadata' : ''}?message=${newMsg}`);
    const text = await ajax.text();
    sethistory(pre => [...pre, { id: 'm_' + msgCount.current++, content: text }])
  }


  return (<>
    <input value={msg} onChange={e => setMsg(e.target.value)} />
    <label><input type="checkbox" onChange={handleOptChange} checked={opt.isStream} name="isStream" />stream(未完成)</label>
    <label><input type="checkbox" onChange={handleOptChange} checked={opt.isMetadata} name="isMetadata" />metadata</label>
    <button onClick={handleClick}>問ai</button>
    <div>
      {history.map(item => <p key={item.id}>{item.content}</p>)}
    </div>
  </>);
}