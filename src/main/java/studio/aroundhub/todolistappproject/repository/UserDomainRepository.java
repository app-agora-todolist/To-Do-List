package studio.aroundhub.todolistappproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.aroundhub.todolistappproject.domain.UserDomain;

import java.util.Optional;

@Repository
public interface UserDomainRepository extends JpaRepository<UserDomain, Long> {
    Optional<UserDomain> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

