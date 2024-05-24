package com.neupinion.neupinion.auth.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.neupinion.neupinion.auth.application.AuthService;
import com.neupinion.neupinion.auth.application.TokenPair;
import com.neupinion.neupinion.auth.application.dto.LoginResponse;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

class AuthControllerTest extends RestAssuredSpringBootTest {

    @Value("${cookie.valid-time}")
    private int cookieAge;

    @MockBean
    private AuthService authService;

    @DisplayName("GET /login/google?code=accessCode 로 요청이 들어오면 상태코드 200과 액세스 토큰, 쿠키를 반환한다.")
    @Test
    void login_success() {
        //given
        final TokenPair tokenPair = new TokenPair("accessToken", "refreshToken");

        when(authService.oAuthLogin(any(String.class), any(String.class))).thenReturn(tokenPair);

        //when
        final var response = RestAssured.given().log().all()
            .when().log().all().get("/login/google?code=accessCode")
            .then().log().all().statusCode(HttpStatus.OK.value())
            .extract();

        //then
        final LoginResponse expectResponseBody = new LoginResponse(tokenPair.getAccessToken());
        final LoginResponse responseBody = response.body().as(LoginResponse.class);
        final String cookie = response.header("Set-Cookie");

        assertAll(
            () -> assertThat(responseBody.getAccessToken()).isEqualTo(
                expectResponseBody.getAccessToken()),
            () -> assertThat(cookie.contains("refreshToken=refreshToken")).isTrue(),
            () -> assertThat(cookie.contains("Max-Age=" + cookieAge)).isTrue(),
            () -> assertThat(cookie.contains("Path=/reissue")).isTrue(),
            () -> assertThat(cookie.contains("HttpOnly")).isTrue()
        );
    }

}
