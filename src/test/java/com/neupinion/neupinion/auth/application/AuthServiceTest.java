package com.neupinion.neupinion.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.neupinion.neupinion.auth.application.dto.ReissueAccessTokenResponse;
import com.neupinion.neupinion.auth.exception.TokenException;
import com.neupinion.neupinion.auth.repository.InMemoryTokenPairRepository;
import com.neupinion.neupinion.member.application.MemberService;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class AuthServiceTest {

    private static Member savedMember;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OAuthProviderFinder oauthProviderFinder;

    @Autowired
    private MemberService memberService;

    @MockBean
    private GoogleInfoProvider googleInfoProvider;

    @Autowired
    private InMemoryTokenPairRepository inMemoryTokenPairRepository;

    private TokenProvider tokenProvider;

    private String refreshToken;

    private String accessToken;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider(
            100000L,
            1000000L,
            "asdfsdsvsdf2esvsdvsdvs23");
        authService = new AuthService(memberService, oauthProviderFinder, tokenProvider, inMemoryTokenPairRepository);
        savedMember = memberRepository.save(Member.forSave("뉴피니언", "image", OAuthType.GOOGLE, "authKey"));
        refreshToken = tokenProvider.createRefreshToken(savedMember.getId());
        accessToken = tokenProvider.createAccessToken(savedMember.getId());
        inMemoryTokenPairRepository.addOrUpdateTokenPair(refreshToken, accessToken);
    }

    @AfterEach
    void delete() {
        memberRepository.delete(savedMember);
    }

    @Test
    void 구글_소셜_로그인_시_액세스_토큰과_리프레시_토큰을_반환한다() {
        // given
        final String accessToken = "accessToken";

        when(googleInfoProvider.getAccessToken(any(String.class)))
            .thenReturn(accessToken);
        when(googleInfoProvider.getMemberInfo(any(String.class)))
            .thenReturn("authKey");

        // when
        final TokenPair tokenPair = authService.oAuthLogin("GOOGLE", "code");

        // then
        assertAll(
            () -> assertThat(tokenProvider.parseClaims(tokenPair.getAccessToken()).get("memberId", Long.class))
                .isEqualTo(savedMember.getId()),
            () -> assertThat(tokenProvider.parseClaims(tokenPair.getRefreshToken()).get("memberId", Long.class))
                .isEqualTo(savedMember.getId())
        );
    }

    @Test
    void 올바른_토큰_페어가_들어오면_access_토큰을_재발급한다() {
        //given
        //when
        final ReissueAccessTokenResponse result = authService.reissueAccessTokenByRefreshToken(
            refreshToken, accessToken);
        final Long resultId = tokenProvider.parseClaims(result.getAccessToken())
            .get("memberId", Long.class);

        //then
        final String accessToken = tokenProvider.createAccessToken(savedMember.getId());

        final Claims claims = tokenProvider.parseClaims(accessToken);
        final Long expectedId = claims.get("memberId", Long.class);

        assertThat(expectedId).isEqualTo(resultId);
    }

    @Test
    void secret_key가_다른_리프레시_토큰이_들어오면_예외를_던진다() {
        //given
        final TokenProvider inValidTokenProvider = new TokenProvider(
            10L,
            100L,
            "asdzzxcwetg2adfvssd3xZcZXCZvzx");

        final String wrongRefreshToken = inValidTokenProvider.createRefreshToken(savedMember.getId());

        //when
        //then
        assertThatThrownBy(() -> authService.reissueAccessTokenByRefreshToken(wrongRefreshToken, accessToken))
            .isInstanceOf(TokenException.NotIssuedTokenException.class);
    }

    @Test
    void 기간이_만료된_리프레시_토큰이면_예외를_던진다() {
        //given
        final TokenProvider inValidTokenProvider = new TokenProvider(0, 0, "asdfsdsvsdf2esvsdvsdvs23");

        final String refreshToken = inValidTokenProvider.createRefreshToken(savedMember.getId());

        //when
        //then
        assertThatThrownBy(() -> authService.reissueAccessTokenByRefreshToken(refreshToken, accessToken))
            .isInstanceOf(TokenException.ExpiredTokenException.class);
    }
}
