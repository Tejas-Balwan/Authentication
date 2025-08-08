package com.example.authentication.exceptions;

public class PasswordMissMatchException extends RuntimeException{
    public PasswordMissMatchException(String message) {
        super(message);
    }
}
