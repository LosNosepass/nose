package com.adorno.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserCreateDTO(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        Set<String> roles
) {}
