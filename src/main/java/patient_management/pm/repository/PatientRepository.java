package patient_management.pm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import patient_management.pm.entity.Patient;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Patient findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
