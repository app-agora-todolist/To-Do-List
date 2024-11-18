package studio.aroundhub.todolistappproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;
import studio.aroundhub.todolistappproject.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDomain> getCurrentUser(@RequestHeader("Authorization") String token) {
        return authService.getCurrentUser(token);
    }
}