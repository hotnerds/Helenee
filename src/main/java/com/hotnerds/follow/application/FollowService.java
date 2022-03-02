package com.hotnerds.follow.application;

import com.hotnerds.follow.domain.Dto.FollowServiceRequestDto;
import com.hotnerds.follow.domain.Follow;
import com.hotnerds.follow.domain.repository.FollowRepository;
import com.hotnerds.follow.exception.FollowRelationshipExistsException;
import com.hotnerds.follow.exception.FollowRelationshipNotFound;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public void addFollowRelationship(FollowServiceRequestDto followServiceRequestDto) {
        searchFollowerRelationship(followServiceRequestDto).ifPresent(exception -> {
            throw new FollowRelationshipExistsException();
        });
        Follow newFollowRelationship = Follow.builder()
                .follower(userRepository.getById(followServiceRequestDto.getFollowerId()))
                .following(userRepository.getById(followServiceRequestDto.getFollowingId()))
                .build();
        followRepository.save(newFollowRelationship);
    }

    public List<Follow> getAllFollowRelationshipByFollowerId(Long followerId) {
        User user = userRepository.findById(followerId).orElseThrow(UserNotFoundException::new);
        return user.getFollowerList();
    }

    public List<Follow> getAllFollowRelationshipByFollowingId(Long followingId) {
        User user = userRepository.findById(followingId).orElseThrow(UserNotFoundException::new);
        return user.getFollowingList();
    }

    public Follow getFollowRelationshipByFollowerIdAndFollowingId(FollowServiceRequestDto followServiceRequestDto) {
        return searchFollowerRelationship(followServiceRequestDto).orElseThrow(FollowRelationshipNotFound::new);
    }

    public void removeFollowRelationship(FollowServiceRequestDto followServiceRequestDto) {
        Follow followRelationship = searchFollowerRelationship(followServiceRequestDto).orElseThrow(FollowRelationshipNotFound::new);
        followRepository.delete(followRelationship);
    }

    public boolean isFollower(FollowServiceRequestDto followServiceRequestDto) {
        return searchFollowerRelationship(followServiceRequestDto).isPresent();
    }

    public boolean isMutualFollow(FollowServiceRequestDto followServiceRequestDto) {
        return isFollower(followServiceRequestDto) &&
                isFollower(followServiceRequestDto.reverse());
    }

    public Optional<Follow> searchFollowerRelationship(FollowServiceRequestDto followServiceRequestDto) {
        User user = userRepository.findById(followServiceRequestDto.getFollowerId()).orElseThrow(UserNotFoundException::new);
        Follow followRelationship = Follow.builder()
                .follower(userRepository.getById(followServiceRequestDto.getFollowerId()))
                .following(userRepository.getById(followServiceRequestDto.getFollowingId()))
                .build();
        return user.getFollowerList().stream()
                .filter(i -> i.equals(followRelationship))
                .findAny();
    }
}
