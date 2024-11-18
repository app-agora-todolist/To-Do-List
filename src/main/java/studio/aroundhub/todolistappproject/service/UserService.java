package studio.aroundhub.todolistappproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.repository.UserDomainRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserDomainRepository userDomainRepository;

    @Autowired
    public UserService(UserDomainRepository userDomainRepository) {
        this.userDomainRepository = userDomainRepository;
    }

    // 사용자 이름으로 사용자 찾기
    public Optional<UserDomain> getUserByUsername(String username) {
        return userDomainRepository.findByUsername(username);
    }
}

