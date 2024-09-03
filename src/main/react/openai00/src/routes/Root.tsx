import { Link, Outlet } from "react-router-dom";

export default function() {
  return (<>
    <nav>
      <span>spring ai測試</span>
      <ul>
        <li>
          <Link to={'/chat-client'}>Check Client</Link>
        </li>
        <li>
          <Link to={'/chat-model'}>Check Model</Link>
        </li>
        <li>
          <Link to={'/gen-img'}>Gen Image</Link>
        </li>
      </ul>
    </nav>
    <div>
      <Outlet></Outlet>
    </div>
  </>);
}