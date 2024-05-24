package com.neupinion.neupinion.auth.application;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    private static final String COOKIE_NAME = "refreshToken";

    private final int cookieAge;

    public CookieProvider(@Value("${cookie.valid-time}") final int cookieAge) {
        this.cookieAge = cookieAge;
    }

    public Cookie createCookieByRefreshToken(final String refreshToken) {
        final Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);
        cookie.setMaxAge(cookieAge);
        cookie.setPath("/reissue");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }
}
