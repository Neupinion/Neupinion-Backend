package com.neupinion.neupinion.auth.ui;

import com.neupinion.neupinion.auth.application.AuthService;
import com.neupinion.neupinion.auth.application.CookieProvider;
import com.neupinion.neupinion.auth.application.TokenPair;
import com.neupinion.neupinion.auth.application.dto.LoginResponse;
import com.neupinion.neupinion.auth.application.dto.ReissueAccessTokenResponse;
import com.neupinion.neupinion.auth.exception.TokenException.RefreshTokenNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String EMPTY_REFRESH_TOKEN = "none";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @GetMapping("/login/{oAuthType}")
    public ResponseEntity<LoginResponse> googleLogin(
        @RequestParam("code") final String authorizationCode,
        @PathVariable("oAuthType") final String oAuthType,
        final HttpServletResponse response
    ) {
        final TokenPair tokenPair = authService.oAuthLogin(oAuthType, authorizationCode);
        final Cookie cookie = cookieProvider.createCookieByRefreshToken(tokenPair.getRefreshToken());
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponse(tokenPair.getAccessToken()));
    }

    @GetMapping("/reissue")
    public ResponseEntity<ReissueAccessTokenResponse> reissueAccessToken(
        @CookieValue(value = REFRESH_TOKEN_KEY, defaultValue = EMPTY_REFRESH_TOKEN) final String refreshToken,
        @RequestHeader(HttpHeaders.AUTHORIZATION) final String authorization
    ) {
        if (refreshToken.equals(EMPTY_REFRESH_TOKEN)) {
            throw new RefreshTokenNotFoundException();
        }

        final String accessToken = authorization.split(TOKEN_PREFIX)[1];
        final ReissueAccessTokenResponse response = authService.reissueAccessTokenByRefreshToken(
            refreshToken, accessToken);

        return ResponseEntity.ok(response);
    }
}
