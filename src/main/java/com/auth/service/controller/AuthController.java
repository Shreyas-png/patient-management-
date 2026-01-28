package com.auth.service.controller;

import com.auth.service.dto.LoginRequestDTO;
import com.auth.service.dto.LoginResponseDTO;
import com.auth.service.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("auth/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest){
        log.info("Request has reached /login controller with email {}", loginRequest.getEmail());

        String token = authService.authenticate(loginRequest);
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .token(token)
                .build();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
