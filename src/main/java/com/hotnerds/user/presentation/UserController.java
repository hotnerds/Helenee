package com.hotnerds.user.presentation;

import com.hotnerds.common.security.oauth2.annotation.Authenticated;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.dto.FollowServiceReqDto;
import com.hotnerds.user.domain.dto.NewUserReqDto;
import com.hotnerds.user.domain.dto.UserResponseDto;
import com.hotnerds.user.domain.dto.UserUpdateReqDto;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.hotnerds.user.presentation.UserController.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class UserController {
    public static final String DEFAULT_URL = "/users";
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(
                users.stream()
                        .map(user -> UserResponseDto.of(user))
                        .collect(Collectors.toList())
        );
    }

    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody NewUserReqDto requestData) { // 일단 username, email만 있는 DTO
        userService.createNewUser(requestData);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(UserResponseDto.of(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateReqDto userInfoReqDto) {
        userService.updateUser(id, userInfoReqDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{followedId}/follow")
    public ResponseEntity<Void> createFollow(@PathVariable("followedId") Long followedId, @Authenticated AuthenticatedUser user) {
        userService.createFollow(new FollowServiceReqDto(user.getId(), followedId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{followedId}/follow")
    public ResponseEntity<Integer> deleteFollow(@PathVariable("followedId") Long followedId, @Authenticated AuthenticatedUser user) {
        userService.deleteFollow(new FollowServiceReqDto(user.getId(), followedId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{followedId}/follow/check")
    public ResponseEntity<Boolean> checkFollow(@PathVariable("followedId") Long followedId, @Authenticated AuthenticatedUser user) {
        return ResponseEntity.ok(userService.followCheck(new FollowServiceReqDto(user.getId(), followedId)));
    }

    @GetMapping("/{userId}/follower")
    public ResponseEntity<List<User>> getFollower(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(userService.getUserFollowers(id));
    }

    @GetMapping("/{userId}/followed")
    public ResponseEntity<List<User>> getFollowed(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(userService.getUserFollowings(id));
    }

    @GetMapping("/{userId}/follower/count")
    public ResponseEntity<Integer> getFollowerCounts(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(userService.getFollowerCounts(id));
    }

    @GetMapping("/{userId}/followed/count")
    public ResponseEntity<Integer> getFollowedCounts(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(userService.getFollowCounts(id));
    }

}

