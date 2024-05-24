package com.neupinion.neupinion.auth.application;

public interface OAuthInfoProvider {

    String getAccessToken(final String authorizationCode);

    String getMemberInfo(final String accessToken);
}
