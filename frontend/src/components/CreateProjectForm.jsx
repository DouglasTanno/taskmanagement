import { useState } from "react";
import api from "../api/api";

import {
    Stack,
    TextField,
    Button,
    Typography,
    IconButton
} from "@mui/material";

import CloseIcon from "@mui/icons-material/Close";


function CreateProjectForm({
                               onCreated,
                               onClose
                           }) {

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");


    async function handleSubmit(event) {

        event.preventDefault();


        try {

            await api.post("/projects", {
                name,
                description
            });


            onCreated();


            setName("");
            setDescription("");


        } catch (error) {

            console.log(error);

        }

    }


    return (

        <Stack
            component="form"
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

                <Typography
                    variant="h6"
                    fontWeight={600}
                >
                    Novo Projeto
                </Typography>


                <IconButton
                    onClick={onClose}
                >
                    <CloseIcon />
                </IconButton>


            </Stack>


            <TextField
                label="Nome"
                value={name}
                onChange={(e) =>
                    setName(e.target.value)
                }
                required
            />


            <TextField
                label="Descrição"
                multiline
                rows={3}
                value={description}
                onChange={(e) =>
                    setDescription(e.target.value)
                }
            />


            <Stack
                direction="row"
                spacing={1}
                justifyContent="flex-end"
            >

                <Button
                    onClick={onClose}
                >
                    Cancelar
                </Button>


                <Button
                    variant="contained"
                    type="submit"
                >
                    Criar Projeto
                </Button>

            </Stack>


        </Stack>

    );

}

export default CreateProjectForm;