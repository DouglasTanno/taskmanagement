import { Navigate } from "react-router-dom";

function Home() {

    const token = localStorage.getItem("token");

    return token
        ? <Navigate to="/dashboard" replace />
        : <Navigate to="/login" replace />;

}

export default Home;