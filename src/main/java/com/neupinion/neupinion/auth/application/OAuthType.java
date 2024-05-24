package com.neupinion.neupinion.auth.application;

import com.neupinion.neupinion.auth.exception.OAuthException.NotFoundOAuthTypeException;
import java.util.Arrays;

public enum OAuthType {
    GOOGLE
    ;

    public static OAuthType from(String type) {
        return Arrays.stream(OAuthType.values())
            .filter(oAuthType -> oAuthType.name().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(NotFoundOAuthTypeException::new);
    }
}
