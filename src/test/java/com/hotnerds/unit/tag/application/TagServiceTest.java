package com.hotnerds.unit.tag.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.tag.application.TagService;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.tag.domain.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagService tagService;

    Tag tag;

    @BeforeEach
    void init() {
        tag = new Tag(1L, "name");
    }

    @DisplayName("태그가 존재하면 조회한다.")
    @Test
    void 태그_조회_성공() {
        //given
        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(tag));

        //when
        Tag findTag = tagService.findOrCreateTag(tag.getName());

        //then
        assertAll(
                () -> assertThat(findTag.getId()).isNotNull(),
                () -> assertThat(findTag.getName()).isEqualTo(tag.getName()));

        verify(tagRepository, times(1)).findByName(anyString());
    }

    @DisplayName("태그가 존재하지 않으면 생성한다.")
    @Test
    void 태그_존재하지않을시_생성() {
        //given
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenReturn(tag);

        //when
        Tag findTag = tagService.findOrCreateTag(tag.getName());

        //then
        assertAll(
                () -> assertThat(findTag.getId()).isNotNull(),
                () -> assertThat(findTag.getName()).isEqualTo(tag.getName()));

        verify(tagRepository, times(1)).findByName(anyString());
        verify(tagRepository, times(1)).save(any());
    }

    @DisplayName("생성할 태그의 이름이 잘못되면 예외가 발생한다.")
    @Test
    void 태그_이름_잘못되면_예외_발생() {
        //given
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> tagService.findOrCreateTag(""))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);

        verify(tagRepository, times(1)).findByName(anyString());
    }

}