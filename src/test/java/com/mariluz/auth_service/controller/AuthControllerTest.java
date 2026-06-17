package com.mariluz.auth_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.LoginRequest;
import com.mariluz.auth_service.dto.RegisterRequest;
import com.mariluz.auth_service.exceptions.EmailAlreadyInUseException;
import com.mariluz.auth_service.exceptions.InvalidCredentialsException;
import com.mariluz.auth_service.security.JwtUtil;
import com.mariluz.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtro JWT y seguridad para ejecutar el test
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper; // para mapear objetos/clases a json

    @MockitoBean
    private AuthService service;

    @MockitoBean
    private JwtUtil jwtUtil; // importante para que funcione el service

    // -------------- Tests Register --------------

    // Codigo 201
    @Test
    public void testRegister() throws Exception {
        // 1. preparar request de prueba
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setName("Test User");
        // 2. preparar respuesta de prueba
        AuthResponse response = AuthResponse.builder()
            .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            .build();
        // 3. generar test
        when(service.register(request)).thenReturn(response);

        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)) // mapeamos la clase de respuesta a json
            )
            .andExpect(status().isCreated());
    }

    // Codigo 400
    @Test
    public void testRegisterValidation() throws Exception {
        // 1. generamos request invalido
        String body = """
            {
              "email": "test@example.com",
              "password": "1234",
              "name": "Test User"
            }
            """;

        // 2. generamos el test
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest()); // 400
    }

    // Codigo 409
    @Test
    public void testRegisterEmailInUse() throws Exception {
        // 1. generamos request invalido
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setName("Test User");

        when(service.register(request)).thenThrow(
            new EmailAlreadyInUseException("El email ya está registrado")
        );

        // 2. generamos el test
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict());
    }

    // -------------- Tests Login

    // Codigo 200
    @Test
    public void testLogin() throws Exception {
        // 1. preparar request de prueba
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        // 2. preparar respuesta de prueba
        AuthResponse response = AuthResponse.builder()
            .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            .build();
        // 3. generar test
        when(service.login(request)).thenReturn(response);

        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)) // mapeamos la clase de respuesta a json
            )
            .andExpect(status().isOk());
    }

    // Codigo 400
    @Test
    public void testLoginInvalidEmail() throws Exception {
        // 1. generamos request invalido
        String body = """
            {
              "email": "testexample.com",
              "password": "1234"
            }
            """;

        // 2. generamos el test
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest()); // 400
    }

    // Codigo 401
    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // 1. generamos request invalido
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        // 2. generamos el test
        when(service.login(request)).thenThrow(
            new InvalidCredentialsException("El email ya está registrado")
        );

        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
    }
}
