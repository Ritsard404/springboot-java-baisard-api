package com.ritsard.baisard.global.auth.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateMemberPasswordDto {
    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    // This method will be called during validation (@Valid)
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordConfirmed() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}