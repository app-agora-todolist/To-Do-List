package studio.aroundhub.todolistappproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.LoginResponse;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;
import studio.aroundhub.todolistappproject.repository.UserDomainRepository;
import studio.aroundhub.todolistappproject.security.JwtTokenProvider;
import studio.aroundhub.todolistappproject.service.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDomainRepository userDomainRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(UserDomainRepository userDomainRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userDomainRepository = userDomainRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest) {
        if (userDomainRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new LoginResponse("Username already exists", null));
        }

        UserDomain user = new UserDomain();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        userDomainRepository.save(user);

        // 고유 토큰 반환
        return ResponseEntity.status(201).body(new LoginResponse("User signed up successfully", user.getToken()));
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        Optional<UserDomain> userOpt = userDomainRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            UserDomain user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // 고유 토큰 반환
                return ResponseEntity.ok(new LoginResponse("Login successful", user.getToken()));
            }
        }

        return ResponseEntity.status(401).body(new LoginResponse("Invalid credentials", null));
    }

    @Override
    public ResponseEntity<LoginResponse> getCurrentUser(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return ResponseEntity.ok(new LoginResponse("Current user: " + username, token));
        }
        return ResponseEntity.status(401).body(new LoginResponse("Invalid token", null));
    }
}