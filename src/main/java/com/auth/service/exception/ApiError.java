package com.auth.service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ApiError {
    private int status;
    private String message;
    private String error;
    private LocalDateTime timeStamp;
}

