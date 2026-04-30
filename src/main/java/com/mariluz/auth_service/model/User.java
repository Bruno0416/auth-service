package com.mariluz.auth_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {

    // id clase tipo UUID (cadena de texto unica)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    // userName: nombre de usuario
    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    @Column(nullable = false, length = 50)
    private String userName;

    // email: por defecto para inicio de sesion
    @Email(message = "Debe ser un correo valido: email@example.com")
    @NotBlank(message = "El email no puede estar vacio")
    @Column(unique = true)
    private String email;

    // password (hash NO contrasenia real)
    @Column(nullable = false)
    private String password;

    // booleano para saber si tiene permisos de admin
    @Column(nullable = false)
    private boolean isAdmin;

    // fecha para saber antiguedad del usuario
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
