package com.autenticacion.GenoSentinelAuth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus asegura que esta excepción devuelve un código de estado 400 (Bad Request)
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid input data")
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}