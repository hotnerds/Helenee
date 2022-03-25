package com.hotnerds.tag.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @DisplayName("태그를 생성할 수 있다.")
    @Test
    void 태그_생성() {
        Tag tag = new Tag("음식");
        assertThat(tag.getName()).isEqualTo("음식");
    }

    @DisplayName("태그 이름은 null, 빈문자열 혹은 최대 길이를 초과하면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    void 태그_소문자_저장(String name) {
        assertThatCode(() -> new Tag(name))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);
    }
}