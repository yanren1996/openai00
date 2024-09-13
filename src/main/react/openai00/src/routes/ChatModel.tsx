import { useRef, useState } from "react";
import ChatRound from "../components/ChatRound";

interface Message {
  id: string,
  question: string,
  opt: Opt
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
    msgCount.current++;
    sethistory(prevHistory => [...prevHistory, { id: '' + msgCount.current, question: msg, opt: opt }]);
    setMsg('');
  }

  return (<>
    <input value={msg} onChange={e => setMsg(e.target.value)} />
    <label><input type="checkbox" onChange={handleOptChange} checked={opt.isStream} name="isStream" />stream</label>
    <label><input type="checkbox" onChange={handleOptChange} checked={opt.isMetadata} name="isMetadata" />metadata</label>
    <button onClick={handleClick}>問ai</button>
    <div>
      {history.map(item => <ChatRound key={item.id} question={item.question} {...item.opt} />)}
    </div>
  </>);
}