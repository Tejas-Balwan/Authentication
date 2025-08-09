package com.example.authentication.controllers;

import com.example.authentication.dtos.LoginRequestDto;
import com.example.authentication.dtos.SignUpRequestDto;
import com.example.authentication.dtos.UserDto;
import com.example.authentication.dtos.ValidateTokenDto;
import com.example.authentication.exceptions.UnAuthorizedException;
import com.example.authentication.models.User;
import com.example.authentication.services.IAuthService;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = authService.signUp(
                signUpRequestDto.getName(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getPhoneNumber()
        );
        return from(user);
    }
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Pair<User, String> response = authService.login(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );

        UserDto userDto = from(response.a);
        String token = response.b;

        MultiValueMap<String , String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, token );

        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);

    }
    @PostMapping("/Validate")
    public boolean validate(@RequestBody ValidateTokenDto validateTokenDto) {
        boolean result = authService.validateToken(
                validateTokenDto.getToken(),
                validateTokenDto.getUserId()
        );
        if (!result) {
            throw new UnAuthorizedException("Invalid token or user ID.");
        }
        return result;
    }

    private UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getUsername());
        return userDto;
    }
    //logout API
    //Forget Password API
}