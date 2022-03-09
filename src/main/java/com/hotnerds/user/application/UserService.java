package com.hotnerds.user.application;

import com.hotnerds.user.domain.Dto.FollowServiceReqDto;
import com.hotnerds.user.domain.Dto.NewUserReqDto;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.FollowRelationshipExistsException;
import com.hotnerds.user.exception.UserExistsException;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createNewUser(NewUserReqDto newUserReqDto) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(newUserReqDto.getUsername(), newUserReqDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserExistsException("동일한 정보를 가진 유저가 이미 존재합니다");
        }

        userRepository.save(newUserReqDto.toEntity());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User ID " + userId + "가 존재하지 않습니다"));
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    public void updateUser(Long userId, UserUpdateReqDto userUpdateReqDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser
                .orElseThrow(() -> new UserNotFoundException("User ID " + userId + "가 존재하지 않습니다"));

        user.updateUser(userUpdateReqDto);

        userRepository.save(user);
    }

    public boolean isFollowExist(User user1, User user2) {
        return user1.isFollowerOf(user2) || user1.isFollowedBy(user2);
    }

    public boolean isMutualFollowExist(User user1, User user2) {
        return user1.isFollowedBy(user2) && user1.isFollowerOf(user2);
    }

    @Transactional
    public Follow createFollow(FollowServiceReqDto followServiceReqDto) {
        User followerUser = getUserById(followServiceReqDto.getFollowerId());
        User followedUser = getUserById(followServiceReqDto.getFollowedId());

        if (isFollowExist(followerUser, followedUser)) {
            throw new FollowRelationshipExistsException("생성하려는 팔로우 관계가 이미 존재합니다.");
        }

        Follow newFollowRelationship = Follow.builder()
                .follower(followerUser)
                .followed(followedUser)
                .build();

        followerUser.getFollowerList().add(newFollowRelationship);
        followedUser.getFollowedList().add(newFollowRelationship);

        return newFollowRelationship;
    }

}
