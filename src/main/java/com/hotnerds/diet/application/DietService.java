package com.hotnerds.diet.application;


import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.dto.DietReadRequestDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.diet.exception.DietNotFoundException;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DietService {

    private final UserRepository userRepository;
    private final DietRepository dietRepository;

    public DietResponseDto findByDateTimeUser(DietReadRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Diet diet = dietRepository.findByDateTimeUser(requestDto.getMealDate(), requestDto.getMealTime(), user)
                .orElseThrow(DietNotFoundException::new);

        return DietResponseDto.of(diet);
    }
}
