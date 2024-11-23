package studio.aroundhub.todolistappproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.repository.UserDomainRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserDomainRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDomainRepository userDomainRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userDomainRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public UserDomain registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserDomain user = new UserDomain();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        return userRepository.save(user);
    }

    // 사용자 이름으로 사용자 찾기
    public Optional<UserDomain> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

