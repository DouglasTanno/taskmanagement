import { useState } from "react";
import api from "../api/api";

import {
    Stack,
    TextField,
    Typography,
    Button,
    IconButton
} from "@mui/material";

import CloseIcon from "@mui/icons-material/Close";

function RegisterForm({ onClose }) {

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    async function handleSubmit(event) {

        event.preventDefault();

        try {

            await api.post("/users", {
                name,
                email,
                password
            });

            alert("Usuário criado com sucesso!");

            onClose();

        } catch (error) {

            console.log(error);

            if (error.response?.data?.detail) {

                alert(error.response.data.detail);

            } else {

                alert("Erro ao criar usuário");

            }

        }

    }

    return (

        <Stack
            component="form"
            autoComplete="off"
            spacing={2}
            onSubmit={handleSubmit}
            sx={{
                p: 3,
                width: 400
            }}
        >

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
            >

                <Typography variant="h6">
                    Criar conta
                </Typography>

                <IconButton onClick={onClose}>
                    <CloseIcon />
                </IconButton>

            </Stack>

            <TextField
                label="Nome"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
            />

            <TextField
                label="E-mail"
                type="email"
                autoComplete="new-email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
            />

            <TextField
                label="Senha"
                type="password"
                autoComplete="new-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
            />

            <Stack
                direction="row"
                spacing={2}
                justifyContent="flex-end"
            >

                <Button
                    type="button"
                    onClick={onClose}
                >
                    Cancelar
                </Button>

                <Button
                    type="submit"
                    variant="contained"
                >
                    Criar Conta
                </Button>

            </Stack>

        </Stack>

    );
}

export default RegisterForm;