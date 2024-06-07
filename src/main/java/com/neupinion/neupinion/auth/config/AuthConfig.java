package com.neupinion.neupinion.auth.config;

import com.neupinion.neupinion.auth.ui.argumentresolver.AuthArgumentResolver;
import com.neupinion.neupinion.auth.ui.interceptor.PathMatcherInterceptor;
import com.neupinion.neupinion.auth.ui.interceptor.PathMethod;
import com.neupinion.neupinion.auth.ui.interceptor.TokenInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor());
    }

    private HandlerInterceptor tokenInterceptor() {
        return new PathMatcherInterceptor(tokenInterceptor)
            .includePathPattern("/reprocessed-issue/opinion", PathMethod.POST)
            .includePathPattern("/reprocessed-issue/**/opinion", PathMethod.POST)
            .includePathPattern("/reprocessed-issue/**/opinion", PathMethod.GET)
            .includePathPattern("/reprocessed-issue/**/opinion/**", PathMethod.POST)
            .includePathPattern("/reprocessed-issue/**/opinion/**", PathMethod.GET)
            .includePathPattern("/reprocessed-issue/**/opinion/**", PathMethod.PUT)
            .includePathPattern("/reprocessed-issue/**/opinion/**", PathMethod.PATCH)
            .includePathPattern("/reprocessed-issue/**/opinion/**", PathMethod.DELETE)
            .includePathPattern("/reprocessed-issue/**/me", PathMethod.GET)
            .includePathPattern("/reprocessed-issue/*", PathMethod.GET)
            .includePathPattern("/reprocessed-issue/*/trust-vote", PathMethod.PUT)
            .includePathPattern("/reprocessed-issue/*/bookmark", PathMethod.PUT)
            .includePathPattern("/follow-up-issue/**", PathMethod.GET)
            .includePathPattern("/follow-up-issue/opinion", PathMethod.POST)
            .includePathPattern("/follow-up-issue/opinion/**", PathMethod.PATCH)
            .includePathPattern("/follow-up-issue/opinion/**", PathMethod.DELETE)
            .includePathPattern("/follow-up-issue/**/me", PathMethod.GET)
            .includePathPattern("/issue/**/opinion/**", PathMethod.GET);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
