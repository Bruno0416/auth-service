package com.mariluz.auth_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "auth_session")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthSession {

    // no lo generamos automaticamente ya que es el mismo token generado con el login o registro del usuario
    @Id
    @Column(nullable = false, unique = true)
    private String token;

    // Relacionamos auth_sessionn con user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // permisos de admin (no puede estar vacio ni cambiarse en la sesion)
    @Column(nullable = false, updatable = false)
    private boolean isAdmin;

    // fecha y hora de creacion en formato local
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // fecha y hora de expiracion en dormato local
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime expiresAt;
}
