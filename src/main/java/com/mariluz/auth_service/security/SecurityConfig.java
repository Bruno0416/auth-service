package com.mariluz.auth_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authProvider;

    /*
    SpringBoot por defecto bloquea los endponits al importar algo del paquete 'security' en este caso el
    encoder y genera una security password para el acceso de developer.
    Uno tiene que manualmente declarar las restricciones de los endpoints cuando pasa esto.

    Declarar acceso sin autenticacion a todos los endpoints de auth
    */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        // Cross Site Request Forgery Spring security lo activa por defecto.
        http
            .csrf(csrf -> csrf.disable()) // Lo desactivamos
            // definimos las reglas de autorizacion para las rutas
            .authorizeHttpRequests(auth ->
                auth

                    .requestMatchers(
                        // Cualquier usuario puede acceder a estas rutas sin estar autenticado
                        "/auth/**",
                        // Documentacion de la API
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    )
                    .permitAll()
                    // Cualquier otra ruta que no haya coincidido con las reglas
                    .anyRequest()
                    //va a requerir autenticacion
                    .authenticated()
            )
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authProvider)
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );
        // construye y devuelve la cadena de filtros que springboot va a usar para validar las request
        return http.build();
    }
}
