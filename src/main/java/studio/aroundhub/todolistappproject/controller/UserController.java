package studio.aroundhub.todolistappproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public UserDomain getUser(@RequestParam String username) {
        Optional<UserDomain> user = userService.getUserByUsername(username);
        return user.orElse(null); // 사용자가 없으면 null 반환
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDomain user) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
