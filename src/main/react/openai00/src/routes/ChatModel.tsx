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
    const current = msgCount.current++; //訊息計數器
    sethistory(pre => [...pre, { id: 'm_' + current, content: newMsg }]);
    // 發送伺服器&印出ai回應
    if (!opt.isStream) {
      await call(newMsg, opt.isMetadata);
    } else {
      stream(newMsg, opt.isMetadata);
    }
  }

  const call = async (newMsg: string, isMetadata: boolean) => {
    const current = msgCount.current++;// 訊息計數器
    if (!isMetadata) {
      const ajax = await fetch(`http://localhost:8080/ai/chat-model?message=${newMsg}`);
      const text = await ajax.text();
      sethistory(pre => [...pre, { id: 'm_' + current, content: text }])
    } else {
      const ajax = await fetch(`http://localhost:8080/ai/chat-model/metadata?message=${newMsg}`);
      const json = await ajax.json();
      const text = JSON.stringify(json, null, 2);
      sethistory(pre => [...pre, { id: 'm_' + current, content: text }])
    }
  }

  const stream = (newMsg: string, isMetadata: boolean) => {
    const current = msgCount.current++;// 訊息計數器
    sethistory(pre => [...pre, { id: 'm_' + current, content: '' }]);
    if (!isMetadata) {
      const sse = new EventSource(`http://localhost:8080/ai/chat-model/stream?message=${newMsg}`);
      sse.addEventListener('message', msg => {
        if (msg.data === '%end%') { // ai 說完
          sse.close();
        } else { // ai 接續說
          sethistory(pre => pre.map(item => {
            if (item.id === 'm_' + current) {
              return { ...item, content: item.content + msg.data }
            }
            return item;
          }));
        }
      })
    } else {
      // const sse = new EventSource(`localhost:8080/ai/chat-model/stream/metadata?message=${newMsg}`);
    }
  }

  return (<>
    <input value={msg} onChange={e => setMsg(e.target.value)} />
    <label><input type="checkbox" onChange={handleOptChange} checked={opt.isStream} name="isStream" />stream(未完成)</label>
    <label><input type="checkbox" onChange={handleOptChange} checked={opt.isMetadata} name="isMetadata" />metadata</label>
    <button onClick={handleClick}>問ai</button>
    <div>
      {history.map(item => <pre key={item.id}>{item.content}</pre>)}
    </div>
  </>);
}