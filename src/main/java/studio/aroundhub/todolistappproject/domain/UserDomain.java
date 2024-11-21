package studio.aroundhub.todolistappproject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String token;

    @PrePersist
    public void generateToken() {
        if (this.token == null) {
            this.token = UUID.randomUUID().toString();
        }
    }
}
