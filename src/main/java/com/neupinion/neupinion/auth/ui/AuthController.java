package com.neupinion.neupinion.auth.ui;

import com.neupinion.neupinion.auth.application.AuthService;
import com.neupinion.neupinion.auth.application.CookieProvider;
import com.neupinion.neupinion.auth.application.TokenPair;
import com.neupinion.neupinion.auth.application.dto.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

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
}
