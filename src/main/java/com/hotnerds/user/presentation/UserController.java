package com.hotnerds.user.presentation;

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

    @PostMapping("/follow")
    public ResponseEntity<Void> createFollow(@RequestBody FollowServiceReqDto followServiceReqDto) {
        userService.createFollow(followServiceReqDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/follow")
    public ResponseEntity<Follow> getFollow(@RequestBody FollowServiceReqDto followServiceReqDto) {
        return ResponseEntity.ok(userService.getOneFollow(followServiceReqDto));
    }

    @GetMapping("/follow/check")
    public ResponseEntity<Boolean> checkFollow(@RequestBody FollowServiceReqDto followServiceReqDto) {
        // 이 부분 리팩토링 필요. 지금 건드리지 않는 이유는 isFollowExist 부분 다 바꿔야해서 일단 안 건드리는 중.
        return ResponseEntity.ok(userService.isFollowExist(userService.getUserById(followServiceReqDto.getFollowerId()), userService.getUserById(followServiceReqDto.getFollowerId())));
    }
    
    @GetMapping("/follow/check/mutual")
    public ResponseEntity<Boolean> checkMutualFollow(@RequestBody FollowServiceReqDto followServiceReqDto) {
        // 동일
        return ResponseEntity.ok(userService.isMutualFollowExist(userService.getUserById(followServiceReqDto.getFollowerId()), userService.getUserById(followServiceReqDto.getFollowerId())));
    }

    @GetMapping("/follow/{id}/follower")
    public ResponseEntity<List<Long>> getFollower(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserFollowers(id));
    }

    @GetMapping("/follow/{id}/followed")
    public ResponseEntity<List<Long>> getFollowed(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserFollowings(id));
    }

    @GetMapping("/follow/{id}/follower/counts")
    public ResponseEntity<Integer> getFollowerCounts(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getFollowerCounts(id));
    }

    @GetMapping("/follow/{id}/followed/counts")
    public ResponseEntity<Integer> getFollowedCounts(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getFollowCounts(id));
    }

}

