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


function TaskCard({ task, tasks, onStatusUpdated }) {

    const [editing, setEditing] = useState(false);
    const user = JSON.parse(localStorage.getItem("user"));
    const canManage =
        user.id === task.projectOwnerId ||
        user.id === task.assigneeId ||
        user.id === task.projectOwnerId;

    const canDelete =
        user.id === task.createdById ||
        user.id === task.projectOwnerId;

    const priorityLabels = {
        LOW: "Baixa",
        MEDIUM: "Média",
        HIGH: "Alta",
        CRITICAL: "Crítica"
    };

    async function handleStatusChange(event) {

        const newStatus = event.target.value;

        if (
            task.status === "TODO" &&
            newStatus === "DONE"
        ) {

            alert(
                "Uma tarefa pendente deve estar em andamento antes de ser concluída."
            );

            return;
        }

        if (
            task.status === "DONE" &&
            newStatus === "TODO"
        ) {

            alert(
                "Uma tarefa concluída deve estar em andamento antes de ficar pendente."
            );

            return;
        }

        if (
            task.priority === "CRITICAL" &&
            newStatus === "DONE" &&
            user.role !== "ADMIN"
        ) {

            alert(
                "Apenas administradores podem concluir tarefas críticas."
            );

            return;
        }

        if (
            newStatus === "IN_PROGRESS" &&
            task.status !== "IN_PROGRESS"
        ) {

            const inProgressCount = tasks.filter(t =>
                t.assigneeId === task.assigneeId &&
                t.status === "IN_PROGRESS"
            ).length;

            if (inProgressCount >= 5) {

                alert(
                    "O responsável já possui 5 tarefas em andamento."
                );

                return;
            }
        }

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
                    disabled={!canManage}
                    onChange={handleStatusChange}
                    sx={{ mt: 2 }}
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
                        disabled={!canManage}
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
                        disabled={!canDelete}
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