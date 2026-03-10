package com.adorno.model.dtos;

import java.util.List;

public record JwtResponseDTO(
        String token,
        String type,
        String username,
        List<String> roles
) {
    public JwtResponseDTO(String token, String username, List<String> roles) {
        this(token, "Bearer", username, roles);
    }
}
