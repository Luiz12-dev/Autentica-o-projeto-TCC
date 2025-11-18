package com.barbershop.barbershop_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

    @NotBlank(message = "The email cannot be empty")
    @Email(message = "It must be on emails format")
    String email,

    @NotBlank(message="The password cannot be empty")
    String password
) {



}
