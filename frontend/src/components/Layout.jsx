import Header from "./Header";

import {
    Box,
    Container
} from "@mui/material";

function Layout({ children }) {

    return (

        <Box
            sx={{
                minHeight: "100vh",
                bgcolor: "#f5f7fa"
            }}
        >

            <Header />

            <Container
                maxWidth="lg"
                sx={{
                    pt: 4,
                    pb: 4
                }}
            >

                {children}

            </Container>

        </Box>

    );

}

export default Layout;