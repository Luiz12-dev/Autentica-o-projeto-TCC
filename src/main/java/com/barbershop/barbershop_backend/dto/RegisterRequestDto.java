package com.barbershop.barbershop_backend.dto;

import com.barbershop.barbershop_backend.Enum.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(

    @NotBlank(message = "The name cannot be empty")
    String username,

    @NotBlank(message = "The email cannot be empty")
    @Email(message = "Must be on email format")
    String email,

    @NotBlank(message = "The password cannot be empty")
    String password,

    @NotNull(message = "The role must be declared")
    Role role

) {


}
