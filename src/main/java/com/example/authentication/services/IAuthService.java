package com.example.authentication.services;

import com.example.authentication.models.User;
import org.antlr.v4.runtime.misc.Pair;

public interface IAuthService {
    User signUp(String name, String email, String password, String phoneNumber);

    Pair<User, String> login(String email, String password);

    Boolean validateToken(String token, Long userId);
}
