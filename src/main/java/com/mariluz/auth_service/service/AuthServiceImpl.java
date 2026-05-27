package com.mariluz.auth_service.service;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.RegisterRequest;
import com.mariluz.auth_service.exceptions.EmailAlreadyInUseException;
import com.mariluz.auth_service.exceptions.InvalidCredentialsException;
import com.mariluz.auth_service.model.Role;
import com.mariluz.auth_service.model.User;
import com.mariluz.auth_service.repository.AuthRepo;
import com.mariluz.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepo repo;

    private final PasswordEncoder encoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. verificar si existe el usuario
        if (repo.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException(
                "El correo ya esta registrado."
            );
            // arrojamos excepcion correo en uso
        }
        // 2. si no existe creamos el objeto
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            // guardamos el hash no la contrasenia real
            .password(encoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();

        // creamos la tupla partir del objeto user
        repo.save(user);

        // 3. retornamos la respuesta
        return AuthResponse.builder()
            .token(jwtUtil.generateToken(user))
            .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. autenticar usuario (delegando la validacion de credenciales a AuthenticationManager)
        // en vez de hacerlo de manera manual comparando el hash por medio de encoder.matches
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Credenciales invalidas.");
        }

        // 2. buscar el usuario
        User user = repo
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                new InvalidCredentialsException("Credenciales invalidas.")
            );

        // 3. retornar respuesta
        return AuthResponse.builder()
            .token(jwtUtil.generateToken(user))
            .build();
    }
}
