package com.hotnerds.follow.application;

import com.hotnerds.follow.domain.Dto.FollowServiceRequestDto;
import com.hotnerds.follow.domain.Follow;
import com.hotnerds.follow.domain.FollowId;
import com.hotnerds.follow.domain.repository.FollowRepository;
import com.hotnerds.follow.exception.FollowRelationshipExistsException;
import com.hotnerds.follow.exception.FollowRelationshipNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;

    public void addFollowRelationship(FollowServiceRequestDto followServiceRequestDto) {
        if (isFollower(followServiceRequestDto)) {
            throw new FollowRelationshipExistsException("동일한 정보를 가진 팔로워 관계가 이미 존재합니다");
        }
        Follow newFollow = followServiceRequestDto.toEntity();
        followRepository.save(newFollow);
    }

    public List<Follow> getAllFollowRelationshipByFollowerId(Long followerId) {
        List<Follow> followRelationships = followRepository.findByFollowerId(followerId);
        if (followRelationships.isEmpty()) {
            throw new FollowRelationshipNotFound("해당 ID가 팔로잉하는 계정이 없습니다");
        }
        return followRelationships;
    }

    public List<Follow> getAllFollowRelationshipByFollowingId(Long followingId) {
        List<Follow> followRelationships = followRepository.findByFollowingId(followingId);
        if (followRelationships.isEmpty()) {
            throw new FollowRelationshipNotFound("해당 ID를 팔로잉하는 계정이 없습니다");
        }
        return followRelationships;
    }

    public Optional<Follow> getFollowRelationshipByFollowerIdAndFollowingId(FollowServiceRequestDto followServiceRequestDto) {
        Optional<Follow> followRelationship = followRepository.findByFollowIdAndFollowingId(followServiceRequestDto.getFollowerId(), followServiceRequestDto.getFollowingId());
        if (followRelationship.isEmpty()) {
            throw new FollowRelationshipNotFound("주어진 정보의 팔로우 관계가 존재하지 않습니다");
        }
        return followRelationship;
    }

    public void removeFollowRelationship(FollowServiceRequestDto followServiceRequestDto) {
        if (!isFollower(followServiceRequestDto)) {
            throw new FollowRelationshipNotFound("주어진 정보의 팔로우 관계가 존재하지 않습니다");
        }
        FollowId followId = followServiceRequestDto.toId();
        followRepository.deleteById(followId);
    }

    public boolean isFollower(FollowServiceRequestDto followServiceRequestDto) {
        Optional<Follow> followRelationship = followRepository.findByFollowIdAndFollowingId(followServiceRequestDto.getFollowerId(), followServiceRequestDto.getFollowingId());
        return followRelationship.isPresent();
    }

    public boolean isMutualFollow(FollowServiceRequestDto followServiceRequestDto) {
        return isFollower(followServiceRequestDto) &&
                isFollower(followServiceRequestDto.reverse());
    }
}
