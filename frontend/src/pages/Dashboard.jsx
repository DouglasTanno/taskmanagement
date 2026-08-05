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
    Dialog
} from "@mui/material";


function Dashboard() {

    const [projects, setProjects] = useState([]);
    const [openForm, setOpenForm] = useState(false);


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

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={4}
            >

                <Typography
                    variant="h4"
                >
                    Dashboard
                </Typography>


                <Button
                    variant="contained"
                    onClick={() =>
                        setOpenForm(true)
                    }
                >
                    Novo Projeto
                </Button>

            </Stack>


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


            <Typography
                variant="body1"
                color="text.secondary"
                mb={4}
            >
                Gerencie seus projetos.
            </Typography>


            <Typography
                mb={2}
            >
                Total de projetos: {projects.length}
            </Typography>


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
                        />

                    </Grid>

                ))}

            </Grid>


        </Layout>

    );

}


export default Dashboard;