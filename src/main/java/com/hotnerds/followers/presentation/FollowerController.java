package com.hotnerds.followers.presentation;

import com.hotnerds.followers.application.FollowerService;
import com.hotnerds.followers.domain.Dto.FollowerServiceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hotnerds.followers.presentation.FollowerController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class FollowerController {
    public static final String DEFAULT_URL = "/followers";

    private final FollowerService followerService;

    @PostMapping("/")
    public ResponseEntity<Void> createFollowerRelationship(@RequestBody FollowerServiceRequestDto followerServiceRequestDto) {
        followerService.addFollowerRelationship(followerServiceRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteFollowerRelationship(@RequestBody FollowerServiceRequestDto followerServiceRequestDto) {
        followerService.removeFollowerRelationship(followerServiceRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mutual")
    public ResponseEntity<Boolean> isMutual(@RequestBody FollowerServiceRequestDto followerServiceRequestDto) {
        return ResponseEntity.ok(followerService.isMutualFollow(followerServiceRequestDto));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Long> getFollowerNumbers(@PathVariable Long userId) {
        // get total number of users that userId User is following
        Long followerNo = followerService.getAllFollowerRelationshipByFollowerId(userId).stream()
                .count();
        return ResponseEntity.ok(followerNo);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<Long> getFollowingNumbers(@PathVariable Long userId) {
        // get total number of users that follows userId User
        Long followingNo = followerService.getAllFollowerRelationshipByFollowingId(userId).stream()
                .count();
        return ResponseEntity.ok(followingNo);
    }

    @GetMapping("/{userId}/{followingId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, @PathVariable Long followingId) {
        FollowerServiceRequestDto followerServiceRequestDto = FollowerServiceRequestDto.Of(userId, followingId);
        return ResponseEntity.ok(followerService.isFollower(followerServiceRequestDto));
    }

}
