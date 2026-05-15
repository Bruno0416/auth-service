package com.mariluz.auth_service.service;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.RegisterRequest;
import com.mariluz.auth_service.exceptions.EmailAlreadyInUseException;
import com.mariluz.auth_service.exceptions.EmailNotFoundException;
import com.mariluz.auth_service.exceptions.InvalidCredentialsException;
import com.mariluz.auth_service.model.Role;
import com.mariluz.auth_service.model.User;
import com.mariluz.auth_service.repository.AuthRepo;
import com.mariluz.auth_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepo repo;

    private final PasswordEncoder encoder;

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(
        AuthRepo repo,
        PasswordEncoder encoder,
        JwtUtil jwtUtil
    ) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
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
            .password(encoder.encode(request.getPassword())) // guardamos el hash no la contrasenia real
            .role(Role.USER)
            .build();

        // creamos la tupla partir del objeto user
        repo.save(user);

        // 3. retornamos la respuesta
        return AuthResponse.builder()
            .userName(user.getName())
            .email(user.getEmail())
            .token(jwtUtil.generateToken(user))
            .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. buscar el correo
        User user = repo
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                new EmailNotFoundException(
                    "El correo no se encuentra registrado."
                )
            );

        // 2. verificar contrasenia
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            // verificamos si la contrasenia coincide
            throw new InvalidCredentialsException("Credenciales invalidas."); // si no arrojamos error de credenciales
        }

        // 3. retornar respuesta
        return AuthResponse.builder()
            .token(jwtUtil.generateToken(user))
            .email(user.getEmail())
            .userName(user.getName())
            .build();
    }
}
