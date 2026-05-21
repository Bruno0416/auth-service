/*
Interfaz --> SOLO define las funciones que va a
    realizar el service no la logica de implementacion.
*/

package com.mariluz.auth_service.service;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.RegisterRequest;

public interface AuthService {
    // register
    AuthResponse register(RegisterRequest request);

    // login
    AuthResponse login(LoginRequest request);
}
