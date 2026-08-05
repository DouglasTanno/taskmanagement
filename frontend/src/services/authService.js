import api from "../api/api";

const TOKEN_KEY = "token";

const authService = {

    async login(email, password) {

        const response = await api.post("/auth/login", {
            email,
            password
        });

        const token = response.data.token;

        localStorage.setItem(TOKEN_KEY, token);

        return response.data;
    },


    logout() {
        localStorage.removeItem(TOKEN_KEY);
    },


    getToken() {
        return localStorage.getItem(TOKEN_KEY);
    },


    isAuthenticated() {
        return !!localStorage.getItem(TOKEN_KEY);
    }

};

export default authService;