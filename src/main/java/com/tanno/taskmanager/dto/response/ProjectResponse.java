package com.tanno.taskmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class ProjectResponse {

    private Long id;

    private String name;

    private String description;

    private Long ownerId;

    private Set<Long> memberIds;
}