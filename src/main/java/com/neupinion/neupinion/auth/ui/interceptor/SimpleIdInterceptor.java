package com.neupinion.neupinion.auth.ui.interceptor;

import com.neupinion.neupinion.auth.ui.AuthContext;
import com.neupinion.neupinion.member.application.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Profile("!prod")
@RequiredArgsConstructor
@Component
public class SimpleIdInterceptor implements HandlerInterceptor {

    private final AuthContext authContext;
    private final MemberService memberService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        final long memberId = Long.parseLong(request.getHeader(HttpHeaders.AUTHORIZATION));
        memberService.validateByIdThrowIfNotExist(memberId);
        authContext.setAuthenticatedMember(memberId);

        return true;
    }
}
