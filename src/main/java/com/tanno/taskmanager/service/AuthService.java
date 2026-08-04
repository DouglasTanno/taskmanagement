package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.LoginRequest;
import com.tanno.taskmanager.dto.response.LoginResponse;
import com.tanno.taskmanager.exception.BusinessException;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.UserRepository;
import com.tanno.taskmanager.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos."));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BusinessException("Email ou senha inválidos.");
        }

        return new LoginResponse(
                jwtService.generateToken(user.getEmail())
        );
    }
}