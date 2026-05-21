package com.mariluz.auth_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "password") // password (hash) excluido para no exponerlo en logs
@EqualsAndHashCode(of = "id") // equals/hashCode solo por id, evita conflictos con colecciones Hibernate
@Entity
@Table(name = "user")
public class User implements UserDetails {

    // id clase tipo UUID (cadena de texto unica)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // username: nombre de usuario
    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    @Column(nullable = false, length = 50, name = "username") // dejamos el nombre username solo para la db
    private String name; // y dejamos el campo de la clase como 'name' para no tener conflictos con springboot security

    // email: por defecto para inicio de sesion
    @Email(message = "Debe ser un correo valido: email@example.com")
    @NotBlank(message = "El email no puede estar vacio")
    @Column(unique = true, length = 150)
    private String email;

    // password (hash NO contrasenia real)
    @Column(nullable = false)
    private String password;

    // enum con el rol
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // fecha para saber antiguedad del usuario
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // retorna el rol asignado al usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
