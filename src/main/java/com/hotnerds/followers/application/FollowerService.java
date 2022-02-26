package com.hotnerds.followers.application;

import com.hotnerds.followers.domain.Dto.AddFollowReqDto;
import com.hotnerds.followers.domain.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowerService {
    private final FollowerRepository followerRepository;

    // addFollow
    public void addFollow(AddFollowReqDto addFollowReqDto) {

    }

    // removeFollow
    // isMutualFollow
}
