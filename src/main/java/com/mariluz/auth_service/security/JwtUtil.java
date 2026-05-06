package com.mariluz.auth_service.security;

import com.mariluz.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

    // extrae el secret de 'application.properties'
    @Value("${jwt.secret}")
    private String jwtSecret;

    // extrae la expiracion (milisegundos) de 'application.properties'
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // crea una variable privada para almacenar la SecretKey
    private SecretKey key;

    // extrae la SecretKey y la almacena en 'key'
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // generar token publico llama al getToken privado
    public String generateToken(User user) {
        // agregar extra claims (datos del usuario) para no requerir un endpoint extra
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("name", user.getName());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole());
        /*
            Guardamos los datos del usuario en el token usando los extraClaims
            para poder extraerlos en el resto de microservicios sin tener que comunicarnos directamente con Auth.
        */
        return getToken(extraClaims, user);
    }

    // getToken genera el token real con claims(role y otros datos) y usuario
    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(key)
            .compact();
    }

    // get username del token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // token validator
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (
            username.equals(userDetails.getUsername()) && !isTokenExpired(token)
        );
    }

    // get claims
    public Claims getAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // get claim (extrae un claim en especifico)
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // obtiene la fecha de expiracion
    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    // valida si el token esta expirado
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
