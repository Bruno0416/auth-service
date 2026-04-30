package com.mariluz.auth_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean //configuracion del encoder para las contrasenias
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                    .requestMatchers("/auth/**") // Cualquier usuario puede acceder a estas rutas sin estar autenticado
                    .permitAll()
                    // Cualquier otra ruta que no haya coincidido con las reglas
                    .anyRequest()
                    //va a requerir autenticacion
                    .authenticated()
            );
        // construye y devuelve la cadena de filtros que springboot va a usar para validar las request
        return http.build();
    }
}
