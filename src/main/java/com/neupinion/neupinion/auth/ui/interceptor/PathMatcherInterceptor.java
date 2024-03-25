package com.neupinion.neupinion.auth.ui.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class PathMatcherInterceptor implements HandlerInterceptor {

    private final HandlerInterceptor handlerInterceptor;
    private final PathContainer pathContainer = new PathContainer();

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        if(pathContainer.isNotIncludedPath(request.getServletPath(), request.getMethod())) {
            return true;
        }
        return handlerInterceptor.preHandle(request, response, handler);
    }

    public PathMatcherInterceptor includePathPattern(final String requestPath, final PathMethod requestPathMethod) {
        pathContainer.includePathPattern(requestPath, requestPathMethod);
        return this;
    }

    public PathMatcherInterceptor excludePathPattern(final String requestPath, final PathMethod requestPathMethod) {
        pathContainer.excludePathPattern(requestPath, requestPathMethod);
        return this;
    }
}
