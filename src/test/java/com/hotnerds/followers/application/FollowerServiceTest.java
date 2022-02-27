package com.hotnerds.followers.application;

import com.hotnerds.followers.domain.Dto.FollowerServiceRequestDto;
import com.hotnerds.followers.domain.Follower;
import com.hotnerds.followers.domain.repository.FollowerRepository;
import com.hotnerds.followers.exception.FollowerRelationshipExistsException;
import com.hotnerds.followers.exception.FollowerRelationshipNotFound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {

    @Mock
    private FollowerRepository followerRepository;

    @InjectMocks
    private FollowerService followerService;
    
    @Test
    @DisplayName("새로운 팔로원 관계를 추가할때 주어진 정보로 이미 존재하는 관계가 있으면 예외 발생")
    void addFollowerException() {
        // given
        FollowerServiceRequestDto testDto = FollowerServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        Follower follower = testDto.toEntity();

        // when
        when(followerRepository.findByFollowerIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.of(follower));

        // then
        assertThrows(FollowerRelationshipExistsException.class, () -> followerService.addFollowerRelationship(testDto));
    }

    @Test
    @DisplayName("해당 ID가 팔로우 하는 모든 ID를 조회, 팔로잉하는 계정이 없으면 예외 발생")
    void getAllFollowerRelationshipByFollowerId() {
        // given
        List<FollowerServiceRequestDto> testDtoList = Arrays.asList(
                FollowerServiceRequestDto.builder()
                        .followerId(1L)
                        .followingId(2L)
                        .build(),
                FollowerServiceRequestDto.builder()
                        .followerId(1L)
                        .followingId(3L)
                        .build()
        );

        // mocking
        when(followerRepository.findByFollowerId(anyLong())).thenReturn(testDtoList.stream()
                .map(reqDto -> reqDto.toEntity())
                .collect(Collectors.toList()));
        when(followerRepository.findByFollowerId(4L)).thenReturn(Arrays.asList());

        // when
        List<Follower> resultList = followerService.getAllFollowerRelationshipByFollowerId(1L);

        // then
        assertEquals(2, resultList.size());
        assertEquals(testDtoList.get(0).toEntity(), resultList.get(0));
        assertEquals(testDtoList.get(1).toEntity(), resultList.get(1));
        assertThrows(FollowerRelationshipNotFound.class, () -> followerService.getAllFollowerRelationshipByFollowerId(4L));
    }

    @Test
    @DisplayName("해당 ID를 팔로우 하는 모든 ID를 조회, 결과가 빈 리스트일 경우 예외 발생")
    void getAllFollowerRelationshipByFollowingId() {
        // given
        List<FollowerServiceRequestDto> testDtoList = Arrays.asList(
                FollowerServiceRequestDto.builder()
                        .followerId(2L)
                        .followingId(1L)
                        .build(),
                FollowerServiceRequestDto.builder()
                        .followerId(3L)
                        .followingId(1L)
                        .build()
        );

        // mocking
        when(followerRepository.findByFollowingId(anyLong())).thenReturn(testDtoList.stream()
                .map(reqDto -> reqDto.toEntity())
                .collect(Collectors.toList()));
        when(followerRepository.findByFollowingId(4L)).thenReturn(Arrays.asList());

        // when
        List<Follower> resultList = followerService.getAllFollowerRelationshipByFollowingId(1L);

        // then
        assertEquals(2, resultList.size());
        assertEquals(testDtoList.get(0).toEntity(), resultList.get(0));
        assertEquals(testDtoList.get(1).toEntity(), resultList.get(1));
        assertThrows(FollowerRelationshipNotFound.class, () -> followerService.getAllFollowerRelationshipByFollowingId(4L));
    }

    @Test
    @DisplayName("특정 팔로워 ID, 팔로잉 ID의 팔로워 관계를 조회, 결과가 빈 리스트일 경우 예외 발생")
    void getFollowerRelationshipByFollowerIdAndFollowingId() {
        // given
        FollowerServiceRequestDto testDto = FollowerServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        FollowerServiceRequestDto failDto = FollowerServiceRequestDto.builder()
                .followerId(4L)
                .followingId(5L)
                .build();
        Follower follower = testDto.toEntity();

        // mocking
        when(followerRepository.findByFollowerIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.of(follower));
        when(followerRepository.findByFollowerIdAndFollowingId(4L, 5L)).thenReturn(Optional.empty());

        // when
        Follower result = followerService.getFollowerRelationshipByFollowerIdAndFollowingId(testDto).get();

        // then
        assertEquals(follower, result);
        assertThrows(FollowerRelationshipNotFound.class, () -> followerService.getFollowerRelationshipByFollowerIdAndFollowingId(failDto));
    }

    @Test
    @DisplayName("팔로워 관계를 삭제할 때 주어진 정보의 관계가 존재하지 않을 때 예외 발생")
    void removeFollowerRelationship() {
        // given
        FollowerServiceRequestDto testDto = FollowerServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();

        // mocking
        when(followerRepository.findByFollowerIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(FollowerRelationshipNotFound.class, () -> followerService.removeFollowerRelationship(testDto));
    }

    @Test
    @DisplayName("특정 팔로워 ID와 팔로잉 ID가 팔로우 관계에 있는지 확인")
    void isFollower() {
        // given
        FollowerServiceRequestDto passTestDto = FollowerServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        FollowerServiceRequestDto failTestDto = FollowerServiceRequestDto.builder()
                .followerId(2L)
                .followingId(3L)
                .build();
        Follower followerPass = passTestDto.toEntity();

        // mocking
        when(followerRepository.findByFollowerIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.of(followerPass));
        when(followerRepository.findByFollowerIdAndFollowingId(2L, 3L)).thenReturn(Optional.empty());

        // when
        boolean passResult = followerService.isFollower(passTestDto);
        boolean failResult = followerService.isFollower(failTestDto);

        // then
        assertTrue(passResult);
        assertFalse(failResult);
    }

    @Test
    @DisplayName("두 아이디가 서로 팔로우 관계에 있는지 확인")
    void isMutualFollow() {
        // given
        FollowerServiceRequestDto passTestDto = FollowerServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        FollowerServiceRequestDto failTestDto = FollowerServiceRequestDto.builder()
                .followerId(100L)
                .followingId(101L)
                .build();
        Follower follower = passTestDto.toEntity();

        // mocking
        when(followerRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.of(follower));
        when(followerRepository.findByFollowerIdAndFollowingId(2L, 1L)).thenReturn(Optional.of(follower));
        when(followerRepository.findByFollowerIdAndFollowingId(100L, 101L)).thenReturn(Optional.of(follower));
        when(followerRepository.findByFollowerIdAndFollowingId(101L, 100L)).thenReturn(Optional.empty());

        // when
        boolean passResult = followerService.isMutualFollow(passTestDto);
        boolean failResult = followerService.isMutualFollow(failTestDto);

        // then
        assertTrue(passResult);
        assertFalse(failResult);
    }
}