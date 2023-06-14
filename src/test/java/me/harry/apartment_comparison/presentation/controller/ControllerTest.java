package me.harry.apartment_comparison.presentation.controller;

import me.harry.apartment_comparison.ApartmentComparisonApplication;
import me.harry.apartment_comparison.domain.model.UserRole;
import me.harry.apartment_comparison.infrastructure.config.RedisConfig;
import me.harry.apartment_comparison.infrastructure.config.WebSecurityConfig;
import me.harry.apartment_comparison.presentation.security.AuthenticationService;
import me.harry.apartment_comparison.presentation.security.TokenGenerator;
import me.harry.apartment_comparison.presentation.security.TokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ContextConfiguration(classes = {
        ApartmentComparisonApplication.class,
        WebSecurityConfig.class,
        RedisConfig.class
})
public abstract class ControllerTest {
    protected static final String USER_ID = "UserId";
    protected static final String ADMIN_ID = "AdminId";

    @SpyBean
    protected AuthenticationService authenticationService;

    @SpyBean
    protected TokenGenerator tokenGenerator;

    @SpyBean
    protected TokenVerifier tokenVerifier;

    protected String userAccessToken;

    protected String adminAccessToken;

    @BeforeEach
    void setUpTokenAndUserDetailsForAuthentication() {
        userAccessToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_USER, Instant.now().plus(5, ChronoUnit.MINUTES));
        adminAccessToken = tokenGenerator.generate(USER_ID, UserRole.ROLE_ADMIN, Instant.now().plus(5, ChronoUnit.MINUTES));


    }
}
