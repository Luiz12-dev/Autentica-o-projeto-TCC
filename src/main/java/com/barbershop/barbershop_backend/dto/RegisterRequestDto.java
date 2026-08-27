package com.barbershop.barbershop_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Contrato do registro publico.
 *
 * NAO exponha 'role' aqui. O endpoint /api/auth/register e permitAll(), entao
 * qualquer campo deste record e controlado pelo cliente. A role e definida
 * pelo servidor em AuthService.register.
 */
public record RegisterRequestDto(

    @NotBlank(message = "The name cannot be empty")
    String username,

    @NotBlank(message = "The email cannot be empty")
    @Email(message = "Must be on email format")
    String email,

    @NotBlank(message = "The password cannot be empty")
    String password

) {


}
