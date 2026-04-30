package com.mariluz.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {

    // No es necesario agrear validaciones
    private String token;
    private String email;
    private String userName;
}
