package me.harry.baedal.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import me.harry.baedal.application.dto.request.SignupServiceRequest;

public record SignupRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$",
                message = "비밀번호는 알파벳 대소문자 + 숫자 + 특수문자[!@#$%^&*()] 조합으로 8~20자로 입력해주세요.")
        String password,
        @NotBlank(message = "이름을 입력해주세요.")
        String name) {
    public SignupServiceRequest toServiceDto() {
        return new SignupServiceRequest(email, password, name);
    }
}
