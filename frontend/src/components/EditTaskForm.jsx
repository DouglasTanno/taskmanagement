import {useEffect, useState} from "react";
import api from "../api/api";

import {
    TextField,
    Button,
    Stack,
    MenuItem
} from "@mui/material";


function EditTaskForm({ task, onUpdated, onCancel }) {

    const [title, setTitle] = useState(task.title);
    const [description, setDescription] = useState(task.description || "");
    const [priority, setPriority] = useState(task.priority);
    const [assigneeId, setAssigneeId] = useState(task.assigneeId);
    const [users, setUsers] = useState([]);

    useEffect(() => {

        async function loadMembers() {

            try {

                const response = await api.get(
                    `/projects/${task.projectId}/members`
                );

                setUsers(response.data);

            } catch (error) {

                console.log(error);

            }

        }


        loadMembers();

    }, [task.projectId]);

    async function handleSubmit(event) {

        event.preventDefault();

        try {

            await api.put(
                `/projects/${task.projectId}/tasks/${task.id}`,
                {
                    title,
                    description,
                    priority,
                    assigneeId: Number(assigneeId)
                }
            );


            onUpdated();


        } catch (error) {

            console.log(error);

        }

    }


    return (

        <Stack
            component="form"
            spacing={2}
            onSubmit={handleSubmit}
            mt={2}
        >

            <TextField
                label="Título"
                value={title}
                onChange={(e) =>
                    setTitle(e.target.value)
                }
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


            <TextField
                select
                label="Prioridade"
                value={priority}
                onChange={(e) =>
                    setPriority(e.target.value)
                }
            >

                <MenuItem value="LOW">
                    Baixa
                </MenuItem>

                <MenuItem value="MEDIUM">
                    Média
                </MenuItem>

                <MenuItem value="HIGH">
                    Alta
                </MenuItem>

                <MenuItem value="CRITICAL">
                    Crítica
                </MenuItem>

            </TextField>

            <TextField
                select
                label="Responsável"
                value={assigneeId}
                onChange={(e) =>
                    setAssigneeId(e.target.value)
                }
            >

                {users.map(user => (

                    <MenuItem
                        key={user.id}
                        value={user.id}
                    >
                        {user.name}
                    </MenuItem>

                ))}

            </TextField>

            <Stack direction="row" spacing={1}>

                <Button
                    variant="contained"
                    type="submit"
                >
                    Salvar
                </Button>


                <Button
                    onClick={onCancel}
                >
                    Cancelar
                </Button>

            </Stack>


        </Stack>

    );

}

export default EditTaskForm;