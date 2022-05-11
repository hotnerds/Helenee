package com.hotnerds.integration.tag;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.integration.IntegrationTest;
import com.hotnerds.tag.application.TagService;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.tag.domain.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TagServiceIntegrationTest extends IntegrationTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagService tagService;

    Tag tag;

    @BeforeEach
    void init() {
        tag = new Tag("name");
    }

    @DisplayName("태그가 존재하면 조회한다.")
    @Test
    void 태그_조회_성공() {
        //given
        Tag savedTag = tagRepository.save(tag);

        //when
        Tag findTag = tagService.findOrCreateTag(savedTag.getName());

        //then
        assertAll(
                () -> assertThat(findTag.getId()).isNotNull(),
                () -> assertThat(findTag.getName()).isEqualTo(tag.getName()));
    }

    @DisplayName("태그가 존재하지 않으면 생성한다.")
    @Test
    void 태그_존재하지않을시_생성() {
        //when
        Tag findTag = tagService.findOrCreateTag(tag.getName());

        //then
        assertAll(
                () -> assertThat(findTag.getId()).isNotNull(),
                () -> assertThat(findTag.getName()).isEqualTo(tag.getName()));
    }

    @DisplayName("생성할 태그의 이름이 잘못되면 예외가 발생한다.")
    @Test
    void 태그_이름_잘못되면_예외_발생() {
        //when then
        assertThatThrownBy(() -> tagService.findOrCreateTag(""))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);
    }
}
