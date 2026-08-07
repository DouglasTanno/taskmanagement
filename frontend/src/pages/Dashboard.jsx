import { useEffect, useState } from "react";
import api from "../api/api";

import Layout from "../components/Layout";
import ProjectCard from "../components/ProjectCard";
import CreateProjectForm from "../components/CreateProjectForm";

import {
    Stack,
    Typography,
    Button,
    Grid,
    Dialog,
    Paper,
    Box
} from "@mui/material";


function Dashboard() {

    const [projects, setProjects] = useState([]);
    const [openForm, setOpenForm] = useState(false);
    const user = JSON.parse(localStorage.getItem("user"));


    async function loadProjects() {

        try {

            const response = await api.get("/projects");

            setProjects(response.data);

        } catch (error) {

            console.log(error);

        }

    }


    useEffect(() => {

        loadProjects();

    }, []);


    return (

        <Layout>


            <Paper
                elevation={2}
                sx={{
                    p: 4,
                    mb: 4,
                    borderRadius: 3
                }}
            >

                <Stack
                    spacing={2}
                >

                    <Typography
                        variant="h4"
                        fontWeight={700}
                    >
                        Dashboard
                    </Typography>


                    <Typography
                        variant="body1"
                        color="text.secondary"
                    >
                        Gerencie seus projetos e acompanhe suas tarefas.
                    </Typography>


                    <Button
                        variant="contained"
                        sx={{
                            width: "fit-content"
                        }}
                        onClick={() =>
                            setOpenForm(true)
                        }
                        disabled={user.role !== "ADMIN"}
                    >
                        Novo Projeto
                    </Button>

                </Stack>

            </Paper>



            <Dialog
                open={openForm}
                onClose={() =>
                    setOpenForm(false)
                }
            >

                <CreateProjectForm
                    onCreated={() => {

                        setOpenForm(false);
                        loadProjects();

                    }}

                    onClose={() =>
                        setOpenForm(false)
                    }
                />

            </Dialog>



            <Paper
                elevation={1}
                sx={{
                    p: 3,
                    mb: 4,
                    borderRadius: 3
                }}
            >

                <Typography
                    variant="h6"
                    fontWeight={600}
                >
                    Meus Projetos
                </Typography>


                <Typography
                    color="text.secondary"
                    mt={1}
                >
                    Total de projetos: {projects.length}
                </Typography>


            </Paper>



            <Grid
                container
                spacing={3}
            >

                {projects.map(project => (

                    <Grid
                        item
                        xs={12}
                        md={6}
                        lg={4}
                        key={project.id}
                    >

                        <ProjectCard
                            project={project}
                            onStatusUpdated={loadProjects}
                        />

                    </Grid>

                ))}


            </Grid>


        </Layout>

    );

}


export default Dashboard;