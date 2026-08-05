import { useNavigate } from "react-router-dom";

import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Box
} from "@mui/material";


function Header() {

    const navigate = useNavigate();


    function getUserName() {

        const token = localStorage.getItem("token");

        if (!token) {
            return "";
        }


        try {

            const payload = JSON.parse(
                atob(token.split(".")[1])
            );


            return payload.sub || payload.username || "";


        } catch (error) {

            return "";

        }

    }


    function handleLogout() {

        localStorage.removeItem("token");

        navigate("/login");

    }


    return (

        <AppBar
            position="static"
            elevation={1}
        >

            <Toolbar>

                <Typography
                    variant="h6"
                    sx={{
                        fontWeight: 700,
                        cursor: "pointer"
                    }}
                    onClick={() => navigate("/dashboard")}
                >
                    Task Manager
                </Typography>


                <Box
                    sx={{
                        flexGrow: 1
                    }}
                />


                <Button
                    color="inherit"
                    onClick={() => navigate("/dashboard")}
                    sx={{
                        mr: 2
                    }}
                >
                    Dashboard
                </Button>


                <Typography
                    sx={{
                        mr: 2
                    }}
                >
                    {getUserName()}
                </Typography>


                <Button
                    color="inherit"
                    onClick={handleLogout}
                >
                    Sair
                </Button>


            </Toolbar>

        </AppBar>

    );

}

export default Header;