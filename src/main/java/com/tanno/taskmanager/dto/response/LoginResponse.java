package com.tanno.taskmanager.dto.response;

import com.tanno.taskmanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long id;
    private String name;
    private Role role;
}