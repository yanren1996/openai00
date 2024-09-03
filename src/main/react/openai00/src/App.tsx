import { createBrowserRouter, RouterProvider } from "react-router-dom"
import Root from "./routes/Root";
import ChatModel from "./routes/ChatModel";

function App() {

  const router = createBrowserRouter([
    {
      path: '/',
      element: <Root></Root>,
      children: [
        {
          path: '/chat-model',
          element: <ChatModel />
        },
        {
          path: '/chat-client',
          element: <span>this is Client</span>
        },
        {
          path: '/gen-img',
          element: <span>this is Image</span>
        }
      ]
    }
  ]);

  return (
    <>
      <RouterProvider router={router}></RouterProvider>
    </>
  )
}

export default App
