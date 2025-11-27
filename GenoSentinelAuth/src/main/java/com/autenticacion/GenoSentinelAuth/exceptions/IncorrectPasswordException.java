package com.autenticacion.GenoSentinelAuth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta excepción se lanza si la contraseña es incorrecta
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Incorrect password")
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}