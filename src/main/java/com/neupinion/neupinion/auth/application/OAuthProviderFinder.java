package com.neupinion.neupinion.auth.application;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuthProviderFinder {

    private final Map<OAuthType, OAuthInfoProvider> oAuthExecution = new HashMap<>();
    private final GoogleInfoProvider googleInfoProvider;

    @PostConstruct
    public void init() {
        oAuthExecution.put(OAuthType.GOOGLE, googleInfoProvider);
    }

    public OAuthInfoProvider findOAuthProvider(final OAuthType oAuthType) {
        return oAuthExecution.get(oAuthType);
    }

}
