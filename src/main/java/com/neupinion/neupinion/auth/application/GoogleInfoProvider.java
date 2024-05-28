package com.neupinion.neupinion.auth.application;

import static com.neupinion.neupinion.auth.exception.OAuthException.GoogleServerException;
import static com.neupinion.neupinion.auth.exception.OAuthException.InvalidAccessTokenException;

import com.neupinion.neupinion.auth.application.dto.GoogleAccessTokenResponse;
import com.neupinion.neupinion.auth.application.dto.GoogleMemberInfoResponse;
import com.neupinion.neupinion.auth.exception.OAuthException.InvalidAuthorizationCodeException;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class GoogleInfoProvider implements OAuthInfoProvider {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String GRANT_TYPE = "authorization_code";

    @Value("${oauth2.google.access-token-url}")
    private String GOOGLE_ACCESS_TOKEN_URL;

    @Value("${oauth2.google.member-info-url}")
    private String GOOGLE_MEMBER_INFO_URL;

    @Value("${oauth2.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${oauth2.google.redirect-uri}")
    private String LOGIN_REDIRECT_URL;

    private final RestTemplate restTemplate;

    @Override
    public String getAccessToken(final String authorizationCode) {
        try {
            final Map<String, String> parameters = Map.of(
                "code", authorizationCode,
                "client_id", GOOGLE_CLIENT_ID,
                "client_secret", GOOGLE_CLIENT_SECRET,
                "redirect_uri", LOGIN_REDIRECT_URL,
                "grant_type", GRANT_TYPE
            );

            final ResponseEntity<GoogleAccessTokenResponse> response = restTemplate.postForEntity(
                GOOGLE_ACCESS_TOKEN_URL,
                parameters,
                GoogleAccessTokenResponse.class
            );

            return Objects.requireNonNull(response.getBody()).getAccessToken();
        } catch (HttpClientErrorException e) {
            throw new InvalidAuthorizationCodeException(
                Map.of("invalidAuthorizationCode", authorizationCode, "error", e.getMessage()));
        } catch (HttpServerErrorException | NullPointerException e) {
            throw new GoogleServerException(Map.of("authorizationCode", authorizationCode));
        }
    }

    @Override
    public String getMemberInfo(final String accessToken) {
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + accessToken);
            final HttpEntity<Object> request = new HttpEntity<>(headers);

            final ResponseEntity<GoogleMemberInfoResponse> response = restTemplate.exchange(
                GOOGLE_MEMBER_INFO_URL,
                HttpMethod.GET,
                request,
                GoogleMemberInfoResponse.class
            );

            return Objects.requireNonNull(response.getBody()).getEmail();
        } catch (HttpClientErrorException e) {
            throw new InvalidAccessTokenException(Map.of("invalidAccessToken", accessToken));
        } catch (HttpServerErrorException | NullPointerException e) {
            throw new GoogleServerException(Map.of("accessToken", accessToken));
        }
    }
}
