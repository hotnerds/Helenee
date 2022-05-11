package com.hotnerds.unit.user.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.FollowedList;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FollowedListTest {

    private Follow followRelationship;
    private FollowedList followedList;

    @BeforeEach
    void setUp() {
        User follower = new User("follower", "email");
        User followed = new User("followed", "email");
        followRelationship = new Follow(1L, follower, followed);
        followedList = FollowedList.empty();
    }

    @DisplayName("이미 존재하는 팔로우 관계를 다시 추가하려고 하는 경우 예외 발생")
    @Test
    void FollowedList_추가_실패() {
        // given
        followedList.getFollowed().add(followRelationship);

        // when then
        assertThatThrownBy(() -> followedList.add(followRelationship))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FOLLOW_DUPLICATED_EXCEPTION);
    }

    @DisplayName("없는 팔로우 관계를 삭제하려고 하는 경우 예외 발생")
    @Test
    void FollowedList_삭제_실패() {
        assertThatThrownBy(() -> followedList.delete(followRelationship))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FOLLOW_NOT_FOUND_EXCEPTION);
    }

}