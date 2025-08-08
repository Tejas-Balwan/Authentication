package com.example.authentication.exceptions;

public class UserNotRegisterException extends RuntimeException {
    public UserNotRegisterException(String message) {
        super(message);
    }
}
