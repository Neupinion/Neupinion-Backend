package com.neupinion.neupinion.auth.config;

import com.neupinion.neupinion.auth.ui.argumentresolver.AuthArgumentResolver;
import com.neupinion.neupinion.auth.ui.interceptor.PathMatcherInterceptor;
import com.neupinion.neupinion.auth.ui.interceptor.TokenInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Profile("!local")
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor());
    }

    private HandlerInterceptor tokenInterceptor() {
        return new PathMatcherInterceptor(tokenInterceptor);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
