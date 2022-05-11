package com.hotnerds.common.security;

import io.restassured.RestAssured;
import io.restassured.internal.ResponseSpecificationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matcher.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SecurityConfigTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8083;
    }

    @DisplayName("OAuth 로그인을 요청하면 OAuth인증창이 뜬다.")
    @Test
    public void OAUTH로그인_요청시_인증창() {
        //Found와 같은 Redirect 응답을 하면 Controller 리턴하는 Redirect 응답이 오는 것이
        //아니라 Redirect 처리가 디ㅗ어
        given()
        .when()
        .redirects().follow(false)
        .get("/oauth2/authorization/kakao")
        .then()
        .statusCode(HttpStatus.FOUND.value())
        .log().all()
        .assertThat()
        .header("Location", containsString("https://kauth.kakao.com/oauth/authorize"));
    }

}