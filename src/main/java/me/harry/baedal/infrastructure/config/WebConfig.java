package me.harry.baedal.infrastructure.config;

import me.harry.baedal.presentation.interceptor.UserRoleValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserRoleValidationInterceptor userRoleValidationInterceptor;

    public WebConfig(UserRoleValidationInterceptor userRoleValidationInterceptor) {
        this.userRoleValidationInterceptor = userRoleValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userRoleValidationInterceptor)
                .addPathPatterns("/api/v1/menus");
    }
}
