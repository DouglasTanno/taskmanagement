import { useEffect, useState } from "react";
import api from "../api/api";

import {
    Stack,
    TextField,
    Button,
    Typography,
    IconButton,
    Autocomplete,
    Chip
} from "@mui/material";

import CloseIcon from "@mui/icons-material/Close";


function EditProjectForm({
                             project,
                             onUpdated,
                             onClose
                         }) {

    const [name, setName] = useState(project.name);
    const [description, setDescription] = useState(
        project.description || ""
    );
    const [users, setUsers] = useState([]);
    const [members, setMembers] = useState([]);



    async function loadData() {

        try {
            const usersResponse =
                await api.get("/users");

            const membersResponse =
                await api.get(
                    `/projects/${project.id}/members`
                );

            setUsers(
                usersResponse.data
            );

            setMembers(
                membersResponse.data.map(
                    user => user.id
                )
            );
        } catch (error) {

            console.log(error);

            alert("Erro ao carregar membros");

        }

    }

    useEffect(() => {

        loadData();

    }, [project.id]);

    async function handleSubmit(event) {

        event.preventDefault();

        try {

            await api.put(
                `/projects/${project.id}`,
                {
                    name,
                    description
                }
            );

            onUpdated();
            onClose();

        } catch (error) {

            console.log(error);

            alert(
                "Erro ao atualizar projeto"
            );

        }

    }

    async function handleMembersChange(
        event,
        newValue,
        reason,
        details
    ) {


        const oldMembers = members;
        const newMembers =
            newValue.map(
                user => user.id
            );

        try {
            if (reason === "removeOption") {


                const removedUserId =
                    oldMembers.find(
                        id =>
                            !newMembers.includes(id)
                    );

                await api.delete(
                    `/projects/${project.id}/members/${removedUserId}`
                );

            }

            if (reason === "selectOption") {


                const addedUserId =
                    details.option.id;


                await api.post(
                    `/projects/${project.id}/members/${addedUserId}`
                );
            }

            setMembers(newMembers);


        } catch (error) {

            console.log(error);

            alert(
                "Erro ao alterar membros"
            );

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
                <Typography variant="h6">
                    Editar Projeto
                </Typography>

                <IconButton onClick={onClose}>
                    <CloseIcon />
                </IconButton>
            </Stack>

            <TextField
                label="Nome"
                value={name}
                onChange={(e) =>
                    setName(e.target.value)
                }
                required
            />

            <TextField
                label="Descrição"
                multiline
                rows={3}
                value={description}
                onChange={(e) =>
                    setDescription(e.target.value)
                }
            />

            <Autocomplete
                multiple
                options={users}
                getOptionLabel={(user) =>
                    user.name
                }

                value={
                    users.filter(user =>
                        members.includes(user.id)
                    )
                }

                onChange={handleMembersChange}


                renderTags={(value, getTagProps) =>
                    value.map((user, index) => (

                        <Chip
                            label={user.name}
                            {...getTagProps({
                                index
                            })}
                        />

                    ))
                }


                renderInput={(params) => (

                    <TextField
                        {...params}
                        label="Membros"
                        placeholder="Adicionar membro"
                    />

                )}

            />

            <Stack
                direction="row"
                justifyContent="flex-end"
                spacing={2}
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
                    Salvar
                </Button>

            </Stack>
        </Stack>

    );

}


export default EditProjectForm;