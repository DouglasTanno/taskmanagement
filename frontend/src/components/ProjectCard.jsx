import { useNavigate } from "react-router-dom";

import {
    Card,
    CardContent,
    CardActions,
    Typography,
    Button
} from "@mui/material";


function ProjectCard({ project }) {

    const navigate = useNavigate();


    return (

        <Card
            elevation={2}
            sx={{
                height: "100%",
                display: "flex",
                flexDirection: "column"
            }}
        >

            <CardContent
                sx={{
                    flexGrow: 1
                }}
            >

                <Typography
                    variant="h6"
                    fontWeight={600}
                >
                    {project.name}
                </Typography>


                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                        mt: 1,
                        display: "-webkit-box",
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: "vertical",
                        overflow: "hidden"
                    }}
                >
                    {project.description || "Sem descrição"}
                </Typography>


            </CardContent>


            <CardActions
                sx={{
                    justifyContent: "flex-end",
                    px: 2,
                    pb: 2
                }}
            >

                <Button
                    variant="outlined"
                    onClick={() =>
                        navigate(`/projects/${project.id}`)
                    }
                >
                    Abrir Projeto
                </Button>


            </CardActions>


        </Card>

    );

}


export default ProjectCard;