package studio.aroundhub.todolistappproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.LoginResponse;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;
import studio.aroundhub.todolistappproject.security.JwtTokenProvider;
import studio.aroundhub.todolistappproject.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 사용자의 인증 정보 확인
        boolean isAuthenticated = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Invalid credentials", null));
        }

        // 인증 성공 시 JWT 토큰 생성
        String token = jwtTokenProvider.createToken(loginRequest.getUsername());
        return ResponseEntity.ok(new LoginResponse("Login successful", token));
    }

    // JWT 토큰으로 현재 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDomain> getCurrentUser(@RequestHeader("Authorization") String token) {
        // "Bearer " 접두사 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        UserDomain currentUser = authService.getCurrentUser(token).getBody();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(currentUser);
    }
}