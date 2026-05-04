package com.mariluz.auth_service.dto;

import com.mariluz.auth_service.model.Role;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MeResponse {

    private UUID id;
    private String username;
    private String email;
    private Role rol;
    private LocalDateTime createdAt;
}
