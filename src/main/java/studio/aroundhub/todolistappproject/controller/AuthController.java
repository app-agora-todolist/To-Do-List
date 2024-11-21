package studio.aroundhub.todolistappproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.LoginResponse;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;
import studio.aroundhub.todolistappproject.service.AuthService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입 API
    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        try {
            LoginResponse response = authService.signUp(signUpRequest).getBody();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest).getBody();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid credentials", null));
        }
    }

    // 현재 사용자 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        return authService.getCurrentUser(token);
    }
}
