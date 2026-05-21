package com.mariluz.auth_service.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {

    // No es necesario agrear validaciones en el response
    private String token;
    private String email;
    private String name;
}
