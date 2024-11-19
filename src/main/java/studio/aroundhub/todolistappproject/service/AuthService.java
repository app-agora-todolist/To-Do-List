package studio.aroundhub.todolistappproject.service;

import org.springframework.http.ResponseEntity;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;

public interface AuthService {
    ResponseEntity<String> signUp(SignUpRequest signUpRequest);
    ResponseEntity<String> login(LoginRequest loginRequest);
    ResponseEntity<UserDomain> getCurrentUser(String token);

    boolean authenticate(String username, String password);
}