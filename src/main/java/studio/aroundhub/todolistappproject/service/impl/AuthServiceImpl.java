package studio.aroundhub.todolistappproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.dto.LoginRequest;
import studio.aroundhub.todolistappproject.dto.SignUpRequest;
import studio.aroundhub.todolistappproject.repository.UserDomainRepository;
import studio.aroundhub.todolistappproject.service.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDomainRepository userDomainRepository;

    @Autowired
    public AuthServiceImpl(UserDomainRepository userDomainRepository) {
        this.userDomainRepository = userDomainRepository;
    }

    @Override
    public ResponseEntity<String> signUp(SignUpRequest signUpRequest) {
        // 사용자 중복 확인
        Optional<UserDomain> existingUser = userDomainRepository.findByUsername(signUpRequest.getUsername());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        // 새로운 사용자 생성 및 저장
        UserDomain newUser = new UserDomain();
        newUser.setUsername(signUpRequest.getUsername());
        newUser.setPassword(signUpRequest.getPassword()); // 실제로는 암호화 필요 (예: BCrypt)
        userDomainRepository.save(newUser);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest) {
        Optional<UserDomain> user = userDomainRepository.findByUsername(loginRequest.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<UserDomain> getCurrentUser(String token) {
        // JWT 토큰에서 사용자 정보 추출하는 로직이 필요합니다. 예제에서는 단순화하였습니다.
        return new ResponseEntity<>(new UserDomain(), HttpStatus.OK);
    }
}
