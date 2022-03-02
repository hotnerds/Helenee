package com.hotnerds.follow.application;

import com.hotnerds.follow.domain.Dto.FollowServiceRequestDto;
import com.hotnerds.follow.domain.Follow;
import com.hotnerds.follow.domain.repository.FollowRepository;
import com.hotnerds.follow.exception.FollowRelationshipExistsException;
import com.hotnerds.follow.exception.FollowRelationshipNotFound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;
    
    @Test
    @DisplayName("새로운 팔로원 관계를 추가할때 주어진 정보로 이미 존재하는 관계가 있으면 예외 발생")
    void addFollowerException() {
        // given
        FollowServiceRequestDto testDto = FollowServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        Follow follow = testDto.toEntity();

        // when
        when(followRepository.findByFollowIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.of(follow));

        // then
        assertThrows(FollowRelationshipExistsException.class, () -> followService.addFollowRelationship(testDto));
    }

    @Test
    @DisplayName("해당 ID가 팔로우 하는 모든 ID를 조회, 팔로잉하는 계정이 없으면 예외 발생")
    void getAllFollowerRelationshipByFollowerId() {
        // given
        List<FollowServiceRequestDto> testDtoList = Arrays.asList(
                FollowServiceRequestDto.builder()
                        .followerId(1L)
                        .followingId(2L)
                        .build(),
                FollowServiceRequestDto.builder()
                        .followerId(1L)
                        .followingId(3L)
                        .build()
        );

        // mocking
        when(followRepository.findByFollowerId(anyLong())).thenReturn(testDtoList.stream()
                .map(reqDto -> reqDto.toEntity())
                .collect(Collectors.toList()));
        when(followRepository.findByFollowerId(4L)).thenReturn(Arrays.asList());

        // when
        List<Follow> resultList = followService.getAllFollowRelationshipByFollowerId(1L);

        // then
        assertEquals(2, resultList.size());
        assertEquals(testDtoList.get(0).toEntity(), resultList.get(0));
        assertEquals(testDtoList.get(1).toEntity(), resultList.get(1));
        assertThrows(FollowRelationshipNotFound.class, () -> followService.getAllFollowRelationshipByFollowerId(4L));
    }

    @Test
    @DisplayName("해당 ID를 팔로우 하는 모든 ID를 조회, 결과가 빈 리스트일 경우 예외 발생")
    void getAllFollowerRelationshipByFollowingId() {
        // given
        List<FollowServiceRequestDto> testDtoList = Arrays.asList(
                FollowServiceRequestDto.builder()
                        .followerId(2L)
                        .followingId(1L)
                        .build(),
                FollowServiceRequestDto.builder()
                        .followerId(3L)
                        .followingId(1L)
                        .build()
        );

        // mocking
        when(followRepository.findByFollowingId(anyLong())).thenReturn(testDtoList.stream()
                .map(reqDto -> reqDto.toEntity())
                .collect(Collectors.toList()));
        when(followRepository.findByFollowingId(4L)).thenReturn(Arrays.asList());

        // when
        List<Follow> resultList = followService.getAllFollowRelationshipByFollowingId(1L);

        // then
        assertEquals(2, resultList.size());
        assertEquals(testDtoList.get(0).toEntity(), resultList.get(0));
        assertEquals(testDtoList.get(1).toEntity(), resultList.get(1));
        assertThrows(FollowRelationshipNotFound.class, () -> followService.getAllFollowRelationshipByFollowingId(4L));
    }

    @Test
    @DisplayName("특정 팔로워 ID, 팔로잉 ID의 팔로워 관계를 조회, 결과가 빈 리스트일 경우 예외 발생")
    void getFollowerRelationshipByFollowerIdAndFollowingId() {
        // given
        FollowServiceRequestDto testDto = FollowServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        FollowServiceRequestDto failDto = FollowServiceRequestDto.builder()
                .followerId(4L)
                .followingId(5L)
                .build();
        Follow follow = testDto.toEntity();

        // mocking
        when(followRepository.findByFollowIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.of(follow));
        when(followRepository.findByFollowIdAndFollowingId(4L, 5L)).thenReturn(Optional.empty());

        // when
        Follow result = followService.getFollowRelationshipByFollowerIdAndFollowingId(testDto).get();

        // then
        assertEquals(follow, result);
        assertThrows(FollowRelationshipNotFound.class, () -> followService.getFollowRelationshipByFollowerIdAndFollowingId(failDto));
    }

    @Test
    @DisplayName("팔로워 관계를 삭제할 때 주어진 정보의 관계가 존재하지 않을 때 예외 발생")
    void removeFollowerRelationship() {
        // given
        FollowServiceRequestDto testDto = FollowServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();

        // mocking
        when(followRepository.findByFollowIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(FollowRelationshipNotFound.class, () -> followService.removeFollowRelationship(testDto));
    }

    @Test
    @DisplayName("특정 팔로워 ID와 팔로잉 ID가 팔로우 관계에 있는지 확인")
    void isFollower() {
        // given
        FollowServiceRequestDto passTestDto = FollowServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        FollowServiceRequestDto failTestDto = FollowServiceRequestDto.builder()
                .followerId(2L)
                .followingId(3L)
                .build();
        Follow followPass = passTestDto.toEntity();

        // mocking
        when(followRepository.findByFollowIdAndFollowingId(anyLong(), anyLong())).thenReturn(Optional.of(followPass));
        when(followRepository.findByFollowIdAndFollowingId(2L, 3L)).thenReturn(Optional.empty());

        // when
        boolean passResult = followService.isFollower(passTestDto);
        boolean failResult = followService.isFollower(failTestDto);

        // then
        assertTrue(passResult);
        assertFalse(failResult);
    }

    @Test
    @DisplayName("두 아이디가 서로 팔로우 관계에 있는지 확인")
    void isMutualFollow() {
        // given
        FollowServiceRequestDto passTestDto = FollowServiceRequestDto.builder()
                .followerId(1L)
                .followingId(2L)
                .build();
        FollowServiceRequestDto failTestDto = FollowServiceRequestDto.builder()
                .followerId(100L)
                .followingId(101L)
                .build();
        Follow follow = passTestDto.toEntity();

        // mocking
        when(followRepository.findByFollowIdAndFollowingId(1L, 2L)).thenReturn(Optional.of(follow));
        when(followRepository.findByFollowIdAndFollowingId(2L, 1L)).thenReturn(Optional.of(follow));
        when(followRepository.findByFollowIdAndFollowingId(100L, 101L)).thenReturn(Optional.of(follow));
        when(followRepository.findByFollowIdAndFollowingId(101L, 100L)).thenReturn(Optional.empty());

        // when
        boolean passResult = followService.isMutualFollow(passTestDto);
        boolean failResult = followService.isMutualFollow(failTestDto);

        // then
        assertTrue(passResult);
        assertFalse(failResult);
    }
}