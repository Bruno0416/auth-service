/*
Clase para autenticar solicitudes REST
*/
package com.mariluz.auth_service.security;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException, java.io.IOException {
        // 1. obtener token de la request
        final String token = getTokenFromRequest(request);
        final String username;

        // 2. retornar si es null
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. obtener username del token
        username = jwtUtil.getUsernameFromToken(token);

        // 4. si el nombre no es null
        if (
            username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(
                username
            );

            // 5. validamos el token
            if (jwtUtil.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                // 6. seteamos los detalles del authToken
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 7. seteamos el authToken al context de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    // funcion para extraer el token
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (
            StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")
        ) {
            return authHeader.substring(7);
        }
        return null;
    }
}
