package com.barbershop.barbershop_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RequestMapping("api/test")
@RestController
@Tag(name = "Módulo de Testes", description = "Endpoints para testes de autorização")
public class TestController {

    @Operation(summary = "Endpoint público de teste", description = "Acessível para qualquer usuário logado")
    @GetMapping("/protected")
    @PreAuthorize("hasAnyRole('CLIENT', 'OWNER')")
    public ResponseEntity<String> getPublicAccess(){
        return ResponseEntity.ok("Access authorized for client and owner");
    }
    @Operation(summary = "Endpoint exclusivo para Clientes", description = "Acessível apenas para usuários com papel de CLIENT")
    @GetMapping("/client-only")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> getClientAccess(){
        return ResponseEntity.ok("Access exclusive for Clients");
    }
    @Operation(summary = "Endpoint exclusivo para Proprietários", description = "Acessível apenas para usuários com papel de OWNER")
    @GetMapping("/owner-only")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> getOwnerAccess(){
        return ResponseEntity.ok("Access exclusive for Owners");
    }

}
