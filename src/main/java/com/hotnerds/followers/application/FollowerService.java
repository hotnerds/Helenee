package com.hotnerds.followers.application;

import com.hotnerds.followers.domain.Dto.FollowerServiceRequestDto;
import com.hotnerds.followers.domain.Follower;
import com.hotnerds.followers.domain.FollowerId;
import com.hotnerds.followers.domain.repository.FollowerRepository;
import com.hotnerds.followers.exception.FollowerRelationshipExistsException;
import com.hotnerds.followers.exception.FollowerRelationshipNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerService {
    private final FollowerRepository followerRepository;

    public void addFollowerRelationship(FollowerServiceRequestDto followerServiceRequestDto) {
        if (isFollower(followerServiceRequestDto)) {
            throw new FollowerRelationshipExistsException("동일한 정보를 가진 팔로워 관계가 이미 존재합니다");
        }
        Follower newFollower = followerServiceRequestDto.toEntity();
        followerRepository.save(newFollower);
    }

    public List<Optional<Follower>> getAllFollowerRelationshipByFollowerId(Long followerId) {
        List<Optional<Follower>> followerRelationships = followerRepository.FindByFollowerId(followerId);
        if (followerRelationships.isEmpty()) {
            throw new FollowerRelationshipNotFound("해당 ID가 팔로잉하는 계정이 없습니다");
        }
        return followerRelationships;
    }

    public List<Optional<Follower>> getAllFollowerRelationshipByFollowingId(Long followingId) {
        List<Optional<Follower>> followerRelationships = followerRepository.FindByFollowingId(followingId);
        if (followerRelationships.isEmpty()) {
            throw new FollowerRelationshipNotFound("해당 ID를 팔로잉하는 계정이 없습니다");
        }
        return followerRelationships;
    }

    public Optional<Follower> getFollowerRelationshipByFollowerIdAndFollowingId(FollowerServiceRequestDto followerServiceRequestDto) {
        Optional<Follower> followerRelationship = followerRepository.FindByFollowerIdAndFollowingId(followerServiceRequestDto.getFollowerId(), followerServiceRequestDto.getFollowingId());
        if (followerRelationship.isEmpty()) {
            throw new FollowerRelationshipNotFound("주어진 정보의 팔로우 관계가 존재하지 않습니다");
        }
        return followerRelationship;
    }

    public void removeFollowerRelationship(FollowerServiceRequestDto followerServiceRequestDto) {
        if (!isFollower(followerServiceRequestDto)) {
            throw new FollowerRelationshipNotFound("주어진 정보의 팔로우 관계가 존재하지 않습니다");
        }
        FollowerId followerId = followerServiceRequestDto.toId();
        followerRepository.deleteById(followerId);
    }

    public boolean isFollower(FollowerServiceRequestDto followerServiceRequestDto) {
        Optional<Follower> followerRelationship = getFollowerRelationshipByFollowerIdAndFollowingId(followerServiceRequestDto);
        return followerRelationship.isPresent();
    }

    public boolean isMutualFollow(FollowerServiceRequestDto followerServiceRequestDto) {
        return isFollower(followerServiceRequestDto) &&
                isFollower(followerServiceRequestDto.reverse());

    }
}
