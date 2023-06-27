package me.harry.baedal.infrastructure.config;

import me.harry.baedal.presentation.security.ExceptionHandlerFilter;
import me.harry.baedal.presentation.security.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    public WebSecurityConfig(ExceptionHandlerFilter exceptionHandlerFilter, TokenAuthenticationFilter tokenAuthenticationFilter) {
        this.exceptionHandlerFilter = exceptionHandlerFilter;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        http.addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, TokenAuthenticationFilter.class);

        http.authorizeHttpRequests(
                requests -> requests
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                        .anyRequest().authenticated()
        );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
