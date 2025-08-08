package com.example.authentication.services;

import com.example.authentication.exceptions.PasswordMissMatchException;
import com.example.authentication.exceptions.UserAlreadySignedUpException;
import com.example.authentication.exceptions.UserNotRegisterException;
import com.example.authentication.models.User;
import com.example.authentication.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepo userRepo;

    public User signUp(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserAlreadySignedUpException("User with this email already exists.");
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        return userRepo.Save(user);
    }
    public User login(String email, String password) {

        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotRegisterException("Please register first.");
        }
        User user = userOptional.get();
        if(!user.getPassword().equals(password)) {
            throw new PasswordMissMatchException("Wrong password, please try again.");
        }

        return user;
    }

    @Override
    public Boolean validate(String token, Long userId) {
        return null;
    }
}
