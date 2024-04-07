package com.neupinion.neupinion.auth.ui.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.util.PathMatcher;

@RequiredArgsConstructor
public class RequestPathPattern {

    private final String path;
    private final PathMethod method;

    public boolean match(final PathMatcher pathMatcher,
                         final String requestPath,
                         final String requestMethod) {
        return pathMatcher.match(path, requestPath) && method.match(requestMethod);
    }
}
