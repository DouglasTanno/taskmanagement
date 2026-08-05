import { useState } from "react";
import authService from "../services/authService";
import { useNavigate } from "react-router-dom";
import RegisterForm from "../components/RegisterForm";

import {
    Box,
    Paper,
    Button,
    Dialog,
    TextField,
    Typography,
    Stack
} from "@mui/material";


function Login() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const [showRegister, setShowRegister] = useState(false);


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

        <Box
            sx={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                backgroundColor: "#f5f5f5"
            }}
        >

            <Paper
                elevation={4}
                sx={{
                    width: 400,
                    p: 4,
                    borderRadius: 3
                }}
            >

                <Stack
                    spacing={3}
                    component="form"
                    onSubmit={handleLogin}
                >

                    <Typography
                        variant="h4"
                        textAlign="center"
                        fontWeight={700}
                    >
                        Task Manager
                    </Typography>


                    <Typography
                        variant="body2"
                        textAlign="center"
                        color="text.secondary"
                    >
                        Faça login para continuar
                    </Typography>


                    <TextField
                        label="Email"
                        type="email"
                        autoComplete="username"
                        value={email}
                        onChange={(e) =>
                            setEmail(e.target.value)
                        }
                        required
                    />


                    <TextField
                        label="Senha"
                        type="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) =>
                            setPassword(e.target.value)
                        }
                        required
                    />


                    <Button
                        type="submit"
                        variant="contained"
                        size="large"
                    >
                        Entrar
                    </Button>


                </Stack>


                <Button
                    fullWidth
                    sx={{
                        mt: 2
                    }}
                    onClick={() =>
                        setShowRegister(true)
                    }
                >
                    Criar conta
                </Button>


            </Paper>


            <Dialog
                open={showRegister}
                onClose={() =>
                    setShowRegister(false)
                }
            >

                <RegisterForm
                    onClose={() =>
                        setShowRegister(false)
                    }
                />

            </Dialog>


        </Box>

    );

}

export default Login;