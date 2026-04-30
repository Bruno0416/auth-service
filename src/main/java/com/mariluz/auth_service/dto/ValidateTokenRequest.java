package com.mariluz.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateTokenRequest {

    @NotBlank(message = "El token no puede estar vacio")
    private String token;
}
