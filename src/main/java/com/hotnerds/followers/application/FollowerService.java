package com.hotnerds.followers.application;

import com.hotnerds.followers.domain.Dto.FollowerServiceRequestDto;
import com.hotnerds.followers.domain.Follower;
import com.hotnerds.followers.domain.FollowerId;
import com.hotnerds.followers.domain.repository.FollowerRepository;
import com.hotnerds.followers.exception.FollowerRelationshipExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerService {
    private final FollowerRepository followerRepository;

    public void addFollowerRelationship(FollowerServiceRequestDto followerServiceRequestDto) {
        if (getFollowerRelationshipByFollowerIdAndFollowingId(followerServiceRequestDto).isPresent()) {
            throw new FollowerRelationshipExistsException("동일한 정보를 가진 팔로워 관계가 있습니다");
        }
        Follower newFollower = followerServiceRequestDto.toEntity();
        followerRepository.save(newFollower);
    }

    public List<Optional<Follower>> getAllFollowerRelationshipByFollowerId(Long followerId) {
        List<Optional<Follower>> followerRelationships = followerRepository.FindByFollowerId(followerId);
        return followerRelationships;
    }

    public List<Optional<Follower>> getAllFollowerRelationshipByFollowingId(Long followingId) {
        List<Optional<Follower>> followerRelationships = followerRepository.FindByFollowerId(followingId);
        return followerRelationships;
    }

    public Optional<Follower> getFollowerRelationshipByFollowerIdAndFollowingId(FollowerServiceRequestDto followerServiceRequestDto) {
        Optional<Follower> followerRelationship = followerRepository.FindByFollowerIdAndFollowingId(followerServiceRequestDto.getFollowerId(), followerServiceRequestDto.getFollowingId());
        return followerRelationship;
    }

    public void removeFollowerRelationship(FollowerServiceRequestDto followerServiceRequestDto) {
        FollowerId followerId = followerServiceRequestDto.toId();
        followerRepository.deleteById(followerId);
    }

    public boolean isFollower(FollowerServiceRequestDto followerServiceRequestDto) {
        Optional<Follower> followerRelationship = followerRepository.FindByFollowerIdAndFollowingId(followerServiceRequestDto.getFollowerId(), followerServiceRequestDto.getFollowingId());
        if(followerRelationship.isPresent()) {
            return true;
        }
        return false;
    }

    public boolean isMutualFollow(FollowerServiceRequestDto followerServiceRequestDto) {
        if (isFollower(followerServiceRequestDto) && isFollower(followerServiceRequestDto.reverse())) {
            return true;
        }
        return false;
    }
}
