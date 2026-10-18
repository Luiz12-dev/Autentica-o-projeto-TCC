package com.barbershop.barbershop_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbershop.barbershop_backend.dto.AuthResponseDto;
import com.barbershop.barbershop_backend.dto.LoginRequestDto;
import com.barbershop.barbershop_backend.dto.RegisterRequestDto;
import com.barbershop.barbershop_backend.dto.RegisterResponseDto;
import com.barbershop.barbershop_backend.security.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Módulo de Autenticação", description = "Endpoints para cadastro e login de usuários")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }



    @Operation(summary = "Cadastra um novo cliente",
    description ="Endpoint público. O perfil é sempre CLIENT, definido pelo servidor; o proprietário não se cadastra por aqui." )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto req){

        RegisterResponseDto response = authService.register(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Realiza o login de um usuário existente", 
    description ="Endpoint público para autenticação de usuários" ) 
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto req){
        AuthResponseDto auth = authService.login(req);

        return ResponseEntity.ok(auth);
    }


}
