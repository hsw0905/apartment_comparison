package me.harry.baedal.presentation.annotation;

import me.harry.baedal.domain.model.user.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleOnly {
    /**
     * 현재 사용자가 해당 역할을 가지고 있는지 확인합니다.
     * @see UserRole
     * @see me.harry.baedal.presentation.interceptor.UserRoleValidationInterceptor
     */
    UserRole[] roles() default {UserRole.ROLE_USER};
}
