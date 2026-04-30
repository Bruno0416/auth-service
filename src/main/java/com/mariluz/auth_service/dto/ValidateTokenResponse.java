package com.mariluz.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateTokenResponse {

    private boolean valid;
    private boolean isAdmin;
}
