package com.example.authentication.controllers;

import com.example.authentication.dtos.LoginRequestDto;
import com.example.authentication.dtos.SignUpRequestDto;
import com.example.authentication.dtos.UserDto;
import com.example.authentication.dtos.ValidateTokenDto;
import com.example.authentication.models.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        UserDto userDto = new UserDto();
        return userDto;
    }
    @PostMapping("/login")
    public UserDto login(@RequestBody LoginRequestDto loginRequestDto) {
        UserDto userDto = new UserDto();
        return userDto;
    }
    @PostMapping("/Validate")
    public boolean validate(@RequestBody ValidateTokenDto validateTokenDto) {
        return true;
    }

    //logout API
    //Forget Password API
}