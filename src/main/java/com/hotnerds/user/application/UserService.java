package com.hotnerds.user.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.domain.dto.*;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createNewUser(final NewUserReqDto newUserReqDto) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(newUserReqDto.getUsername(), newUserReqDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new BusinessException(ErrorCode.USER_DUPLICATED_EXCEPTION);
        }

        userRepository.save(newUserReqDto.toEntity());
    }

    public User getUserById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    }

    @Transactional
    public void deleteUserById(final Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(final Long userId, final UserUpdateReqDto userUpdateReqDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        user.updateUser(userUpdateReqDto);

        userRepository.save(user);
    }

    public boolean isFollowExist(final User follower, final User followed) {
        return follower.isFollowerOf(followed) && followed.isFollowedBy(follower);
    }

    public boolean followCheck(final FollowServiceReqDto reqDto) {
        User follower = getUserById(reqDto.getFollowerId());
        User followed = getUserById(reqDto.getFollowedId());
        return isFollowExist(follower, followed);
    }

    @Transactional
    public Follow createFollow(final FollowServiceReqDto followServiceReqDto) {
        User followerUser = getUserById(followServiceReqDto.getFollowerId());
        User followedUser = getUserById(followServiceReqDto.getFollowedId());

        if (isFollowExist(followerUser, followedUser)) {
            throw new BusinessException(ErrorCode.FOLLOW_DUPLICATED_EXCEPTION);
        }

        Follow newFollowRelationship = Follow.builder()
                .follower(followerUser)
                .followed(followedUser)
                .build();

        followerUser.follow(followedUser);

        return newFollowRelationship;
    }

    public List<User> getUserFollowers(final Long userId) {
        return getUserById(userId).getFollowerList().getFollowers().stream()
                .map(f -> f.getFollower())
                .collect(Collectors.toList());
    }

    public List<User> getUserFollowings(final Long userId) {
        return getUserById(userId).getFollowedList().getFollowed().stream()
                .map(f -> f.getFollowed())
                .collect(Collectors.toList());
    }

    public Integer getFollowerCounts(long userId) {
        return getUserById(userId).getFollowerList().followerCounts();
    }

    public Integer getFollowCounts(long userId) {
        return getUserById(userId).getFollowedList().followCounts();
    }

    @Transactional
    public void deleteFollow(final FollowServiceReqDto followServiceReqDto) {
        User followerUser = getUserById(followServiceReqDto.getFollowerId());
        User followedUser = getUserById(followServiceReqDto.getFollowedId());

        if (!isFollowExist(followerUser, followedUser)) {
            throw new BusinessException(ErrorCode.FOLLOW_NOT_FOUND_EXCEPTION);
        }

        followerUser.unfollow(followedUser);
    }

    @Transactional
    public void createOrChangeGoal(final GoalRequestDto requestDto, final String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        user.addOrChangeGoal(requestDto.toEntity(user));
    }

    public GoalResponseDto findGoalByDate(final LocalDate date, final String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        return GoalResponseDto.of(user.getGoalOfUser(date));
    }
}