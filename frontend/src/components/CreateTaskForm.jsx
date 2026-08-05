import { useState } from "react";
import api from "../api/api";

import {
    Stack,
    TextField,
    Button,
    Typography,
    IconButton,
    MenuItem
} from "@mui/material";

import CloseIcon from "@mui/icons-material/Close";


function CreateTaskForm({
                            projectId,
                            onTaskCreated,
                            onClose
                        }) {

    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [priority, setPriority] = useState("LOW");
    const [assigneeId, setAssigneeId] = useState("");


    async function handleSubmit(event) {

        event.preventDefault();

        try {

            await api.post(`/projects/${projectId}/tasks`, {
                title,
                description,
                priority,
                assigneeId: Number(assigneeId)
            });


            onTaskCreated();


            setTitle("");
            setDescription("");
            setPriority("LOW");
            setAssigneeId("");


        } catch (error) {

            console.log(error);

            alert("Erro ao criar tarefa");

        }

    }


    return (

        <Stack
            component="form"
            onSubmit={handleSubmit}
            spacing={2}
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
                    Nova tarefa
                </Typography>


                <IconButton
                    onClick={onClose}
                >
                    <CloseIcon />
                </IconButton>

            </Stack>


            <TextField
                label="Título"
                value={title}
                onChange={(e) =>
                    setTitle(e.target.value)
                }
                required
            />


            <TextField
                label="Descrição"
                value={description}
                onChange={(e) =>
                    setDescription(e.target.value)
                }
                multiline
                rows={3}
            />


            <TextField
                select
                label="Prioridade"
                value={priority}
                onChange={(e) =>
                    setPriority(e.target.value)
                }
            >

                <MenuItem value="LOW">
                    Baixa
                </MenuItem>

                <MenuItem value="MEDIUM">
                    Média
                </MenuItem>

                <MenuItem value="HIGH">
                    Alta
                </MenuItem>

                <MenuItem value="CRITICAL">
                    Crítica
                </MenuItem>

            </TextField>


            <TextField
                label="ID do responsável"
                type="number"
                value={assigneeId}
                onChange={(e) =>
                    setAssigneeId(e.target.value)
                }
                required
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
                    type="submit"
                    variant="contained"
                >
                    Criar
                </Button>

            </Stack>


        </Stack>

    );

}

export default CreateTaskForm;