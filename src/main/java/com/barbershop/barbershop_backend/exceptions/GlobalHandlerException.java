package com.barbershop.barbershop_backend.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalHandlerException {

    @ExceptionHandler(DuplicatedEmailException.class)
    public String handleDuplicatedEmailException(DuplicatedEmailException ex){
        return ex.getMessage();
    }  
}    