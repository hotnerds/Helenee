package com.hotnerds.fatsecret.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        assertThat(fatSecretResponseErrorHandler.hasError(responseBody)).isEqualTo(true);

    }

    @Test
    @DisplayName("response body에 error 필드가 없고 상태코드가 2XX이면 정상 응답으로 판단한다.")
    void BODY에_ERROR가_없으면_정상_응답으로_판단() throws IOException {
        //given
        byte[] responseBody = "{ }".getBytes(StandardCharsets.UTF_8);

        //when then
        assertThat(fatSecretResponseErrorHandler.hasError(responseBody)).isEqualTo(false);

    }

    @Test
    @DisplayName("response body에 error 필드가 있으면 예외를 발생시킨다.")
    void BODY에_ERROR가_있으면_EXCEPTION_발생() throws IOException {
        //given
        byte[] responseBody = "{ \"error\": { }}\n".getBytes(StandardCharsets.UTF_8);

        //when then
        assertThrows(FatSecretResponseErrorException.class, () -> fatSecretResponseErrorHandler.handleError(responseBody));
    }
}