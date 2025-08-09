package patient_management.pm.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExist.class)
    public ResponseEntity<ApiError> resourceAlreadyExistHandler(ResourceAlreadyExist ex){
        ApiError error = new ApiError(400, ex.getMessage(), "Duplicate Entry", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
