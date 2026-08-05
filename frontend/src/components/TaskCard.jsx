import { useState } from "react";
import api from "../api/api";

import {
    Card,
    CardContent,
    Typography,
    Chip,
    Stack,
    Button,
    Select,
    MenuItem
} from "@mui/material";

import EditTaskForm from "./EditTaskForm";


function TaskCard({ task, onStatusUpdated }) {

    const [editing, setEditing] = useState(false);


    const priorityLabels = {
        LOW: "Baixa",
        MEDIUM: "Média",
        HIGH: "Alta",
        CRITICAL: "Crítica"
    };


    async function handleStatusChange(event) {

        const newStatus = event.target.value;

        try {

            await api.patch(
                `/projects/${task.projectId}/tasks/${task.id}/status`,
                {
                    status: newStatus
                }
            );


            onStatusUpdated();

        } catch (error) {

            console.log(error);

        }

    }


    async function handleDelete() {

        const confirmDelete = window.confirm(
            "Deseja excluir esta tarefa?"
        );


        if (!confirmDelete) {
            return;
        }


        try {

            await api.delete(
                `/projects/${task.projectId}/tasks/${task.id}`
            );


            onStatusUpdated();


        } catch (error) {

            console.log(error);

        }

    }


    if (editing) {

        return (

            <Card elevation={2}>

                <CardContent>

                    <EditTaskForm
                        task={task}
                        onUpdated={() => {

                            setEditing(false);
                            onStatusUpdated();

                        }}
                        onCancel={() =>
                            setEditing(false)
                        }
                    />

                </CardContent>

            </Card>

        );

    }


    return (

        <Card elevation={2}>

            <CardContent>

                <Typography
                    variant="h6"
                    fontWeight={600}
                >
                    {task.title}
                </Typography>


                {task.description && (

                    <Typography
                        variant="body2"
                        color="text.secondary"
                        mt={1}
                    >
                        {task.description}
                    </Typography>

                )}


                <Stack
                    direction="row"
                    spacing={1}
                    mt={2}
                >

                    <Chip
                        label={priorityLabels[task.priority]}
                        size="small"
                    />

                </Stack>


                <Select
                    size="small"
                    value={task.status}
                    onChange={handleStatusChange}
                    sx={{
                        mt: 2
                    }}
                >

                    <MenuItem value="TODO">
                        A Fazer
                    </MenuItem>

                    <MenuItem value="IN_PROGRESS">
                        Em Andamento
                    </MenuItem>

                    <MenuItem value="DONE">
                        Concluído
                    </MenuItem>

                </Select>


                <Stack
                    direction="row"
                    spacing={1}
                    mt={1}
                >

                    <Button
                        variant="text"
                        size="small"
                        onClick={() =>
                            setEditing(true)
                        }
                        sx={{
                            textTransform: "none"
                        }}
                    >
                        Editar
                    </Button>


                    <Button
                        variant="text"
                        color="error"
                        size="small"
                        onClick={handleDelete}
                        sx={{
                            textTransform: "none"
                        }}
                    >
                        Excluir
                    </Button>


                </Stack>


            </CardContent>

        </Card>

    );

}


export default TaskCard;