package com.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChangePasswordRequestDto {

    @NotBlank(message = "new password cannot be empty")
    @Size(min=8, message = "password should be minimum 8 characters")
    private String newPassword;
}
