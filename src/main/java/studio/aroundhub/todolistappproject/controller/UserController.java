package studio.aroundhub.todolistappproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import studio.aroundhub.todolistappproject.domain.UserDomain;
import studio.aroundhub.todolistappproject.service.UserService;

import java.util.Optional;

@RestController
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
}
