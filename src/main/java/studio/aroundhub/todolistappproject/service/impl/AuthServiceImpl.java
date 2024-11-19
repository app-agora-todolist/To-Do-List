package studio.aroundhub.todolistappproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
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
    public ResponseEntity<String> signUp(SignUpRequest signUpRequest) {
        if (userDomainRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        UserDomain newUser = new UserDomain();
        newUser.setUsername(signUpRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userDomainRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest) {
        Optional<UserDomain> userOptional = userDomainRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            UserDomain user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtTokenProvider.createToken(user.getUsername());
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @Override
    public ResponseEntity<UserDomain> getCurrentUser(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            Optional<UserDomain> user = userDomainRepository.findByUsername(username);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(401).build();
    }

    public boolean authenticate(String username, String password) {
        Optional<UserDomain> userOptional = userDomainRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserDomain user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}

