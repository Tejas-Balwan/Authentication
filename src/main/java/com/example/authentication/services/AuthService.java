package com.example.authentication.services;

import com.example.authentication.exceptions.PasswordMissMatchException;
import com.example.authentication.exceptions.UserAlreadySignedUpException;
import com.example.authentication.exceptions.UserNotRegisterException;
import com.example.authentication.models.Status;
import com.example.authentication.models.User;
import com.example.authentication.models.UserSession;
import com.example.authentication.repos.SessionRepo;
import com.example.authentication.repos.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SecretKey secretKey;

    public User signUp(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserAlreadySignedUpException("User with this email already exists.");
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(name);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        return userRepo.save(user);
    }
    public Pair<User, String> login(String email, String password) {

        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotRegisterException("Please register first.");
        }
        User user = userOptional.get();
        //if(!user.getPassword().equals(password)) {
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMissMatchException("Wrong password, please try again.");
        }

        //Token generation
//        String message = "{\n" +
//                "  \"userId\": \"" + user.getId() + "\",\n" +
//                "  \"email\": \"" + user.getEmail() + "\",\n" +
//                "  \"username\": \"" + user.getUsername() + "\"\n" +
//                "}";
//
//        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", user.getId());
        Long currentTime = System.currentTimeMillis();
        claims.put("gen",currentTime);
        claims.put("exp",currentTime + 1000 * 60 * 60 * 24); // 1 day expiration
        claims.put("access",user.getRoles());

        // These lines are now declared into the SecurityConfig class
//        MacAlgorithm algorithm = Jwts.SIG.HS256;
//        SecretKey secretKey = algorithm.key().build();
        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        UserSession userSession = new UserSession();
        userSession.setToken(token);
        userSession.setUser(user);
        userSession.setStatus(Status.ACTIVE);
        sessionRepo.save(userSession);

        return new Pair<User,String>(user, token);
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        Optional<UserSession> userSessionOptional = sessionRepo.findByTokenAndUser_Id(token, userId);
        if(userSessionOptional.isEmpty()){
            return false;
        }

        JwtParser jswtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jswtParser.parseSignedClaims(token).getPayload();

        Long tokenExpiry = (Long)claims.get("exp");
        Long currentTime = System.currentTimeMillis();

        if(tokenExpiry < currentTime) {
            UserSession userSession = userSessionOptional.get();
            userSession.setStatus(Status.INACTIVE);
            sessionRepo.save(userSession);
            return false; // Token has expired
        }

        return true;
    }
}
