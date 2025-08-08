package com.example.authentication.services;

import com.example.authentication.models.User;

public interface IAuthService {
    User signUp(String name, String email, String password, String phoneNumber);

    User login(String email, String password);

    Boolean validate(String token, Long userId);
}
