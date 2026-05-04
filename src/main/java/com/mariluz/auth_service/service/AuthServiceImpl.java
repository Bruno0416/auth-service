package com.mariluz.auth_service.service;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.MeResponse;
import com.mariluz.auth_service.dto.RegisterRequest;
import com.mariluz.auth_service.exceptions.EmailAlreadyInUseException;
import com.mariluz.auth_service.exceptions.EmailNotFoundException;
import com.mariluz.auth_service.exceptions.InvalidCredentialsException;
import com.mariluz.auth_service.model.Role;
import com.mariluz.auth_service.model.User;
import com.mariluz.auth_service.repository.AuthRepo;
import com.mariluz.auth_service.security.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepo repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

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
            .username(request.getUserName())
            .email(request.getEmail())
            .password(encoder.encode(request.getPassword())) // guardamos el hash no la contrasenia real
            .role(Role.USER)
            .build();

        // creamos la tupla partir del objeto user
        repo.save(user);

        // 3. retornamos la respuesta
        return AuthResponse.builder()
            .userName(user.getUsername())
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
            .userName(user.getUsername())
            .build();
    }

    @Override
    public MeResponse me() {
        // 1. extraer el correo del usuario (ya debe haber sido autenticado por el protocolo de seguridad)
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        // 2. buscar al usuario en la db y extraer sus datos
        Optional<User> userOpt = repo.findByEmail(email);

        //*** no deberia ocurrir, pero por si a caso agregamos la validacion **
        if (userOpt.isEmpty()) {
            throw new EmailNotFoundException(
                "El correo no se encuentra registrado"
            );
        }

        User user = userOpt.get();

        // 3. construir y retornar MeResponse
        return MeResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .rol(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
