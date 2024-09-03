import { createBrowserRouter, RouterProvider } from "react-router-dom"
import Root from "./routes/Root";

function App() {

  const router = createBrowserRouter([
    {
      path: '/',
      element: <Root></Root>,
      children:[
        {
          path: '/chat-client',
          element: <span>this is client</span>
        },
        {
          path: '/chat-model',
          element: <span>this is Model</span>
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
