package com.tanno.taskmanager.service;

import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário autenticado não encontrado"));
    }
}