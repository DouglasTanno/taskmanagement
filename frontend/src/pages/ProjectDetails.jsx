import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";

import TaskCard from "../components/TaskCard";
import CreateTaskForm from "../components/CreateTaskForm";
import Layout from "../components/Layout";

import {
    Stack,
    Typography,
    Paper,
    Divider,
    Button,
    Dialog
} from "@mui/material";


function ProjectDetails() {

    const { projectId } = useParams();

    const [tasks, setTasks] = useState([]);

    const [project, setProject] = useState(null);

    const [summary, setSummary] = useState(null);

    const [openTaskForm, setOpenTaskForm] = useState(false);


    async function loadTasks() {

        try {

            const response = await api.get(
                `/projects/${projectId}/tasks`
            );

            setTasks(response.data);

        } catch (error) {

            console.log(error);

        }

    }

    async function loadProject() {

        try {

            const response = await api.get(
                `/projects/${projectId}`
            );

            setProject(response.data);

        } catch (error) {

            console.log(error);

        }

    }


    async function loadSummary() {

        try {

            const response = await api.get(
                `/projects/${projectId}/tasks/summary`
            );

            setSummary(response.data);

        } catch (error) {

            console.log(error);

        }

    }


    useEffect(() => {

        loadTasks();
        loadProject();
        loadSummary();

    }, [projectId]);


    const columns = [
        {
            key: "TODO",
            label: "A Fazer"
        },
        {
            key: "IN_PROGRESS",
            label: "Em Andamento"
        },
        {
            key: "DONE",
            label: "Concluído"
        }
    ];


    return (

        <Layout>

            <Typography
                variant="h4"
                mb={1}
                fontWeight={600}
            >
                {project?.name}
            </Typography>


            <Typography
                color="text.secondary"
                mb={3}
            >
                {project?.description || "Sem descrição"}
            </Typography>

            {summary && (

                <Stack
                    direction="row"
                    spacing={2}
                    mb={3}
                >

                    <Paper
                        sx={{
                            p: 2,
                            flex: 1
                        }}
                    >
                        <Typography>
                            A Fazer
                        </Typography>

                        <Typography
                            variant="h5"
                            fontWeight={600}
                        >
                            {summary.byStatus.TODO || 0}
                        </Typography>

                    </Paper>


                    <Paper
                        sx={{
                            p: 2,
                            flex: 1
                        }}
                    >
                        <Typography>
                            Em Andamento
                        </Typography>

                        <Typography
                            variant="h5"
                            fontWeight={600}
                        >
                            {summary.byStatus.IN_PROGRESS || 0}
                        </Typography>

                    </Paper>


                    <Paper
                        sx={{
                            p: 2,
                            flex: 1
                        }}
                    >
                        <Typography>
                            Concluídas
                        </Typography>

                        <Typography
                            variant="h5"
                            fontWeight={600}
                        >
                            {summary.byStatus.DONE || 0}
                        </Typography>

                    </Paper>


                </Stack>

            )}

            <Button
                variant="contained"
                onClick={() =>
                    setOpenTaskForm(true)
                }
            >
                Nova Tarefa
            </Button>


            <Dialog
                open={openTaskForm}
                onClose={() =>
                    setOpenTaskForm(false)
                }
            >

                <CreateTaskForm
                    projectId={projectId}
                    onTaskCreated={() => {

                        setOpenTaskForm(false);
                        loadTasks();

                    }}
                    onClose={() =>
                        setOpenTaskForm(false)
                    }
                />

            </Dialog>

            <Divider sx={{ my: 4 }} />

            <Stack
                direction="row"
                spacing={3}
                mt={8}
            >

                {columns.map(column => (

                    <Paper
                        key={column.key}
                        elevation={2}
                        sx={{
                            flex: 1,
                            p: 2,
                            minHeight: 350
                        }}
                    >

                        <Typography
                            variant="h6"
                            mb={2}
                            fontWeight={600}
                        >
                            {column.label}
                        </Typography>


                        <Stack spacing={2}>

                            {tasks
                                .filter(task =>
                                    task.status === column.key
                                )
                                .map(task => (

                                    <TaskCard
                                        key={task.id}
                                        task={task}
                                        tasks={tasks}
                                        onStatusUpdated={loadTasks}
                                    />

                                ))
                            }

                        </Stack>


                    </Paper>

                ))}

            </Stack>


        </Layout>

    );

}


export default ProjectDetails;