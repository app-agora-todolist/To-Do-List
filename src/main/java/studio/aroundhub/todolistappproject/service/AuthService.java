package studio.aroundhub.todolistappproject.service;

import org.springframework.http.ResponseEntity;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.LoginResponse;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;

public interface AuthService {
    ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest);

    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    ResponseEntity<LoginResponse> getCurrentUser(String token);
}
