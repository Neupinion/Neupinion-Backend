package com.neupinion.neupinion.auth.application;

import com.neupinion.neupinion.auth.application.dto.ReissueAccessTokenResponse;
import com.neupinion.neupinion.auth.repository.InMemoryTokenPairRepository;
import com.neupinion.neupinion.member.application.MemberService;
import com.neupinion.neupinion.member.domain.Member;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberService memberService;
    private final OAuthProviderFinder oAuthProviderFinder;
    private final TokenProvider tokenProvider;
    private final InMemoryTokenPairRepository inMemoryTokenPairRepository;

    @Transactional
    public TokenPair oAuthLogin(final String type, final String authorizationCode) {
        final OAuthType oAuthType = OAuthType.from(type);
        final OAuthInfoProvider oAuthInfoProvider = oAuthProviderFinder.findOAuthProvider(oAuthType);

        final String oAuthAccessToken = oAuthInfoProvider.getAccessToken(authorizationCode);
        final String authKey = oAuthInfoProvider.getMemberInfo(oAuthAccessToken);

        final Member member = memberService.findByAuthKeyAndAuthType(authKey, oAuthType)
            .orElse(memberService.registerMember(authKey, oAuthType));

        final Long memberId = member.getId();
        final String accessToken = tokenProvider.createAccessToken(memberId);
        final String refreshToken = tokenProvider.createRefreshToken(memberId);
        inMemoryTokenPairRepository.addOrUpdateTokenPair(refreshToken, accessToken);

        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public ReissueAccessTokenResponse reissueAccessTokenByRefreshToken(final String refreshToken,
                                                                       final String accessToken) {
        final Claims claims = tokenProvider.parseClaims(refreshToken);
        final Long memberId = claims.get("memberId", Long.class);

        inMemoryTokenPairRepository.validateTokenPair(refreshToken, accessToken);
        final String reissuedAccessToken = tokenProvider.createAccessToken(memberId);
        inMemoryTokenPairRepository.addOrUpdateTokenPair(refreshToken, reissuedAccessToken);

        return new ReissueAccessTokenResponse(reissuedAccessToken);
    }
}
