package patient_management.pm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PatientRequestDTO {
    @NotNull(message = "name cannot be null")
    private String name;

    @Email(message = "PLease pass valid email")
    private String email;

    @NotNull(message = "Address cannot be null")
    private String address;

    @NotNull(message = "Pass correct date of birth cannot be null")
    private LocalDate dateOfBirth;
}

