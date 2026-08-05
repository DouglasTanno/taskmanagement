import { useState } from "react";
import authService from "../services/authService";
import { useNavigate } from "react-router-dom";

function Login() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();

        try {
            await authService.login(email, password);

            alert("Login realizado!");
            navigate("/dashboard");

        } catch (error) {
            console.log(error);
            alert("Erro no login");
        }
    };


    return (
        <div>
            <h1>Login</h1>

            <form onSubmit={handleLogin}>

                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                <br />

                <input
                    type="password"
                    placeholder="Senha"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <br />

                <button type="submit">
                    Entrar
                </button>

            </form>
        </div>
    );
}

export default Login;