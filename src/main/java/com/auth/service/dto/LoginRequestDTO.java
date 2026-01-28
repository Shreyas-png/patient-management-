package com.auth.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "email is missing")
    @Email(message = "enter valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min=8, message = "password should be minimum 8 characters")
    private String password;

}
