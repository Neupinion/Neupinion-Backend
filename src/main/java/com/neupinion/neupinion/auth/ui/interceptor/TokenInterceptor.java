package com.neupinion.neupinion.auth.ui.interceptor;

import com.neupinion.neupinion.auth.application.TokenProvider;
import com.neupinion.neupinion.auth.exception.TokenException.AccessTokenNotFoundException;
import com.neupinion.neupinion.auth.ui.AuthContext;
import com.neupinion.neupinion.member.application.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final AuthContext authContext;
    private final MemberService memberService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        final String token = TokenHeaderExtractor.extractToken(request)
            .orElseThrow(() -> new AccessTokenNotFoundException(
                Map.of("RequestURL", request.getRequestURL().toString())
            ));
        final Long memberId = tokenProvider.parseClaims(token)
            .get("memberId", Long.class);
        memberService.validateByIdThrowIfNotExist(memberId);

        authContext.setAuthenticatedMember(memberId);
        return true;
    }
}
