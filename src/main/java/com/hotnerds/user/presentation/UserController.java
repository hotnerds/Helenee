package com.hotnerds.user.presentation;

import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hotnerds.user.presentation.UserController.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class UserController {
    public static final String DEFAULT_URL = "/users";
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

}

