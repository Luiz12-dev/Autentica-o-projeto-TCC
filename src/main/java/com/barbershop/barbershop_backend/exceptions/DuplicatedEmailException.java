package com.barbershop.barbershop_backend.exceptions;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String email) {
        super("Email "+ email +" already in use: " );
    }

}
