package com.hotnerds.unit.user.domain;

import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FollowTest {

    private User follower;
    private User followed;
    private Follow followRelationship;

    @BeforeEach
    void setUp() {
        follower = new User("follower", "email");
        followed = new User("followed", "email");
        followRelationship = new Follow(1L, follower, followed);
    }

    @DisplayName("equals 메서드에 대한 검증")
    @Test
    void equals_검증() {
        // given
        Follow anotherFollowRelationship = new Follow(2L, followed, follower);
        Follow identicalFollowRelationship = new Follow(1L, follower, followed);

        // when then
        assertAll(
                () -> assertThat(followRelationship.equals(followRelationship)).isTrue(),
                () -> assertThat(followRelationship.equals(follower)).isFalse(),
                () -> assertThat(followRelationship.equals(anotherFollowRelationship)).isFalse(),
                () -> assertThat(followRelationship.equals(identicalFollowRelationship)).isTrue()
        );
    }

    @DisplayName("동일한 Follow 엔티티는 같은 hashCode를 가지고 있어야 한다")
    @Test
    void hashCode_검증() {
        // given
        Follow identicalFollowRelationship = new Follow(1L, follower, followed);

        assertThat(followRelationship).hasSameHashCodeAs(identicalFollowRelationship);
    }

}