package com.tanno.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequest {

    @NotBlank
    private String name;

    private String description;
}