package com.mariluz.auth_service.controller;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.ErrorResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface AuthApi {
    // Register
    @Operation(
        summary = "Registro de cliente",
        description = "Registra un nuevo cliente en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente. Devuelve un token JWT.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los campos enviados (ej. email inválido, contraseña corta).",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/auth/register",
                        "errors": {
                            "email": "El email debe tener un formato válido",
                            "password": "La contraseña no debe estar vacía"
                        },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto. El correo proporcionado ya se encuentra registrado en el sistema.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/auth/register",
                        "errors": null,
                        "message": "El correo ya esta registrado.",
                        "status": 409,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor no controlado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/auth/register",
                        "errors": null,
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<AuthResponse> register(RegisterRequest request);

    // Login
    @Operation(
        summary = "Login de usuario",
        description = "Autentica un usuario en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario autenticado exitosamente. Devuelve un token JWT.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en la estructura de la petición.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/auth/login",
                        "errors": {
                            "email": "El email no debe estar vacío"
                        },
                        "message": "Error de validacion",
                        "status": 400,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No Autorizado. Las credenciales proporcionadas son incorrectas (email no encontrado o contraseña inválida).",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/auth/login",
                        "errors": null,
                        "message": "Credenciales invalidas.",
                        "status": 401,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor no controlado.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "endpoint": "/auth/login",
                        "errors": null,
                        "message": "Error interno del servidor",
                        "status": 500,
                        "timeStamp": "2026-06-12T05:11:58"
                    }
                    """
                )
            )
        ),
    })
    ResponseEntity<AuthResponse> login(LoginRequest request);
}
