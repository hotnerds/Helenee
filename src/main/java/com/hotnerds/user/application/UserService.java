package com.hotnerds.user.application;

import com.hotnerds.user.domain.Dto.FollowServiceReqDto;
import com.hotnerds.user.domain.Dto.NewUserReqDto;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.FollowRelationshipExistsException;
import com.hotnerds.user.exception.FollowRelationshipNotFound;
import com.hotnerds.user.exception.UserExistsException;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createNewUser(final NewUserReqDto newUserReqDto) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(newUserReqDto.getUsername(), newUserReqDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserExistsException("동일한 정보를 가진 유저가 이미 존재합니다");
        }

        userRepository.save(newUserReqDto.toEntity());
    }

    public User getUserById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public void deleteUserById(final Long userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(userId);
    }

    public void updateUser(final Long userId, final UserUpdateReqDto userUpdateReqDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateUser(userUpdateReqDto);

        userRepository.save(user);
    }

    public boolean isFollowExist(final User user1, final User user2) {
        return user1.isFollowerOf(user2) && user2.isFollowedBy(user1);
    }

    public boolean isMutualFollowExist(final User user1, final User user2) {
        return (user1.isFollowerOf(user2) && user2.isFollowedBy(user1))
                && (user1.isFollowedBy(user2) && user2.isFollowerOf(user1));
    }

    @Transactional
    public Follow createFollow(final FollowServiceReqDto followServiceReqDto) {
        User followerUser = getUserById(followServiceReqDto.getFollowerId());
        User followedUser = getUserById(followServiceReqDto.getFollowedId());

        if (isFollowExist(followerUser, followedUser)) {
            throw new FollowRelationshipExistsException("생성하려는 팔로우 관계가 이미 존재합니다.");
        }

        Follow newFollowRelationship = Follow.builder()
                .follower(followerUser)
                .followed(followedUser)
                .build();

        followerUser.follow(followedUser);

        return newFollowRelationship;
    }

    public Follow getOneFollow(final FollowServiceReqDto followServiceReqDto) {
        User followerUser = getUserById(followServiceReqDto.getFollowerId());
        User followedUser = getUserById(followServiceReqDto.getFollowedId());

        if (!isFollowExist(followerUser, followedUser)) {
            throw new FollowRelationshipNotFound("찾으려는 팔로우 관계 정보가 존재하지 않습니다.");
        }

        return followerUser.getFollowedList().getFollowed().stream()
                .filter(f -> f.getFollowed().equals(followedUser))
                .findAny()
                .get();
    }

    public List<Long> getUserFollowers(final Long userId) {
        return getUserById(userId).getFollowerList().getFollowers().stream()
                .map(f -> f.getFollower().getId())
                .collect(Collectors.toList());
    }

    public List<Long> getUserFollowings(final Long userId) {
        return getUserById(userId).getFollowedList().getFollowed().stream()
                .map(f -> f.getFollowed().getId())
                .collect(Collectors.toList());
    }

    public Integer getFollowerCounts(long userId) {
        return getUserById(userId).getFollowerList().followerCounts();
    }

    public Integer getFollowCounts(long userId) {
        return getUserById(userId).getFollowedList().followCounts();
    }

    public void deleteFollow(final FollowServiceReqDto followServiceReqDto) {
        User followerUser = getUserById(followServiceReqDto.getFollowerId());
        User followedUser = getUserById(followServiceReqDto.getFollowedId());

        if (!isFollowExist(followerUser, followedUser)) {
            throw new FollowRelationshipNotFound();
        }

        followerUser.unfollow(followedUser);
    }
}