import {Navigate, Outlet} from "react-router-dom";
import {AppUser} from "./Model.ts";

type ProtectedRoutesProps = {
    user: AppUser | undefined | null
}

export default function ProtectedRoutes(props: ProtectedRoutesProps) {

    if (props.user === undefined) {
        return <div>Loading...</div>
    }

    return props.user ? <Outlet/> : <Navigate to="/login" />
}
