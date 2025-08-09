package patient_management.pm.customexception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private String error;
    private LocalDateTime timeStamp;
}
