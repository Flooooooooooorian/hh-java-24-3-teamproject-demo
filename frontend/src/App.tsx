import {Todo} from "./Todo.ts";
import {useEffect, useState} from "react";
import axios from "axios";
import TodoColumn from "./TodoColumn.tsx";
import {allPossibleTodos} from "./TodoStatus.ts";
import {Route, Routes} from "react-router-dom";
import ProtectedRoutes from "./ProtectedRoutes.tsx";
import {AppUser} from "./Model.ts";

function App() {

    const [todos, setTodos] = useState<Todo[]>([])
    const [user, setUser] = useState<AppUser | null>()

    const login = () => {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + '/oauth2/authorization/github', '_self')
    }

    const logout = () => {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + '/logout', '_self')
    }

    const loadCurrentUser = () => {
        axios.get("/api/users/me")
            .then((response) => {
                setUser(response.data)
            })
            .catch(() => {
                setUser(null)
            })
    }

    function fetchTodos() {
        axios.get("/api/todo")
            .then(response => {
                setTodos(response.data)
            })
    }

    useEffect(fetchTodos, [])
    useEffect(loadCurrentUser, []);

    if (!todos) {
        return "Lade..."
    }

    return (
        <Routes>
            <Route path="/"
                   element={<div className="page">
                       {!user && <button onClick={login}>Login</button>}
                       {user && <button onClick={logout}>Logout</button>}
                       <p>{user?.username}</p>
                       <h1>TODOs</h1>
                       {
                           allPossibleTodos.map(status => {
                               const filteredTodos = todos.filter(todo => todo.status === status)
                               return <TodoColumn
                                   status={status}
                                   todos={filteredTodos}
                                   onTodoItemChange={fetchTodos}
                                   key={status}
                               />
                           })
                       }
                   </div>}/>
            <Route path="/login" element={<button onClick={login}>Login</button>} />

            <Route element={<ProtectedRoutes user={user} />} >
                <Route path="/todo/add" element={<p>Add Todo Page</p>} />
                <Route path="/admin" element={<p>Admin Page</p>} />
            </Route>
        </Routes>
    )
}

export default App
