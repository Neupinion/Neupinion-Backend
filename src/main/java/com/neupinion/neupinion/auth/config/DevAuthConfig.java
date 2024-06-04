package com.neupinion.neupinion.auth.config;

import com.neupinion.neupinion.auth.ui.argumentresolver.AuthArgumentResolver;
import com.neupinion.neupinion.auth.ui.interceptor.PathMatcherInterceptor;
import com.neupinion.neupinion.auth.ui.interceptor.PathMethod;
import com.neupinion.neupinion.auth.ui.interceptor.SimpleIdInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("local, dev")
@RequiredArgsConstructor
@Configuration
public class DevAuthConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final SimpleIdInterceptor simpleIdInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(simpleIdInterceptor());
    }

    public PathMatcherInterceptor simpleIdInterceptor() {
        return new PathMatcherInterceptor(simpleIdInterceptor)
            .includePathPattern("/**", PathMethod.GET)
            .includePathPattern("/**", PathMethod.PUT)
            .includePathPattern("/**", PathMethod.POST)
            .includePathPattern("/**", PathMethod.PATCH)
            .includePathPattern("/**", PathMethod.DELETE);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
