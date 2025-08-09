package patient_management.pm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PatientRequestDTO {
    private String name;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
}

