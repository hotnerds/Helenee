package com.hotnerds.food.infrastructure.fatsecret.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FatSecretResponseErrorHandlerTest {

    FatSecretResponseErrorHandler fatSecretResponseErrorHandler;

    @BeforeEach
    void setUp() {
        fatSecretResponseErrorHandler = new FatSecretResponseErrorHandler(new ObjectMapper());
    }

    @Test
    @DisplayName("response body에 error 필드가 있으면 응답에 에러가 있다고 판단한다.")
    void BODY에_ERROR가_있으면_에러로_판단() throws IOException {
        //given
        byte[] responseBody = "{ \"error\": { }}\n".getBytes(StandardCharsets.UTF_8);

        //when then
        assertThat(fatSecretResponseErrorHandler.hasError(responseBody)).isTrue();

    }

    @Test
    @DisplayName("response body에 error 필드가 없고 상태코드가 2XX이면 정상 응답으로 판단한다.")
    void BODY에_ERROR가_없으면_정상_응답으로_판단() throws IOException {
        //given
        byte[] responseBody = "{ }".getBytes(StandardCharsets.UTF_8);

        //when then
        assertThat(fatSecretResponseErrorHandler.hasError(responseBody)).isFalse();

    }

    @Test
    @DisplayName("response body에 error 필드가 있으면 예외를 발생시킨다.")
    void BODY에_ERROR가_있으면_EXCEPTION_발생() {
        //given
        byte[] responseBody = "{ \"error\": { }}\n".getBytes(StandardCharsets.UTF_8);

        //when then
        assertThatThrownBy(() -> fatSecretResponseErrorHandler.handleError(responseBody))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.EXTERNAL_COMMUNICATION_EXCEPTION.getMessage());
    }
}