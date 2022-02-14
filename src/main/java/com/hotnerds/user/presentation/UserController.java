package com.hotnerds.user.presentation;

import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.DTO.NewUserDto;
import com.hotnerds.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/")
    public void createUser(@RequestBody NewUserDto requestData) { // 일단 username, email만 있는 DTO
        User user = User.builder()
                .username(requestData.getUsername())
                .email(requestData.getEmail())
                .build();
        userService.createNewUser(user);

        // should return a response
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);

        // should return a response
    }

}

