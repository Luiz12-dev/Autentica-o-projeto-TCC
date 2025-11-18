package com.barbershop.barbershop_backend.dto;

public record AuthResponseDto(
String accessToken,

String type

) {

    public AuthResponseDto( String accessToken){

        this(accessToken,"Bearer");

    }

}
