package com.example.cham.controller;

import com.example.cham.model.User;
import com.example.cham.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.cham.service.AuthServiceImpl;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cham/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthServiceImpl AuthServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        User savedUser = userService.registerUser(user.getName(), user.getEmail(), user.getPassword());
        String token = AuthServiceImpl.generateToken(savedUser.getId(), savedUser.getName());
        
        return ResponseEntity.ok(Map.of(
            "message", "Hello " + savedUser.getName() + ", Welcome to CHAMCHAM!",
            "token", token
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please provide email and password"));
        }

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User does not exist. Register and try again"));
        }

        User user = userOptional.get();
        boolean isPasswordCorrect = user.comparePassword(password);
        if (!isPasswordCorrect) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Credentials"));
        }

        String token = AuthServiceImpl.generateToken(user.getId(), user.getName());

        return ResponseEntity.ok(Map.of(
            "name", user.getName(),
            "token", token
        ));
    }
}
