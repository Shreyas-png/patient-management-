package com.auth.service.dto;

import jakarta.validation.constraints.Email;
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
public class CreateUserRequestDto {

    @NotBlank(message = "email is required filed")
    @Email(message = "Provide correct email")
    private String email;

    @NotBlank(message = "Missing password")
    @Size(min = 8, message = "minimum 8 characters required for password")
    private String password;

//    @NotBlank(message = "Missing role")
    private String role;

}
