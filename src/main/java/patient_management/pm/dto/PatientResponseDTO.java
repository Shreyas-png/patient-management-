package patient_management.pm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class PatientResponseDTO {
//    private UUID id;
    private String name;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
}
