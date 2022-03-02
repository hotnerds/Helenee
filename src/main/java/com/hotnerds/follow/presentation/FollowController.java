package com.hotnerds.follow.presentation;

import com.hotnerds.follow.application.FollowService;
import com.hotnerds.follow.domain.Dto.FollowServiceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hotnerds.follow.presentation.FollowController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class FollowController {
    public static final String DEFAULT_URL = "/follow";

    private final FollowService followService;

    @PostMapping("/")
    public ResponseEntity<Void> createFollowerRelationship(@RequestBody FollowServiceRequestDto followServiceRequestDto) {
        followService.addFollowRelationship(followServiceRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteFollowerRelationship(@RequestBody FollowServiceRequestDto followServiceRequestDto) {
        followService.removeFollowRelationship(followServiceRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mutual")
    public ResponseEntity<Boolean> isMutual(@RequestBody FollowServiceRequestDto followServiceRequestDto) {
        return ResponseEntity.ok(followService.isMutualFollow(followServiceRequestDto));
    }

    @GetMapping("/{userId}/follow")
    public ResponseEntity<Long> getFollowerNumbers(@PathVariable Long userId) {
        // get total number of users that userId User is following
        Long followerNo = followService.getAllFollowRelationshipByFollowerId(userId).stream()
                .count();
        return ResponseEntity.ok(followerNo);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<Long> getFollowingNumbers(@PathVariable Long userId) {
        // get total number of users that follows userId User
        Long followingNo = followService.getAllFollowRelationshipByFollowingId(userId).stream()
                .count();
        return ResponseEntity.ok(followingNo);
    }

    @GetMapping("/{userId}/{followingId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, @PathVariable Long followingId) {
        FollowServiceRequestDto followServiceRequestDto = FollowServiceRequestDto.Of(userId, followingId);
        return ResponseEntity.ok(followService.isFollower(followServiceRequestDto));
    }

}
