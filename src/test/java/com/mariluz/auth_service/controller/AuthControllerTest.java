package com.mariluz.auth_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mariluz.auth_service.dto.AuthResponse;
import com.mariluz.auth_service.dto.RegisterRequest;
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
}
