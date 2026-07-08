package com.laptopstore.business.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        super("Authentication required", HttpStatus.UNAUTHORIZED);
    }
}
