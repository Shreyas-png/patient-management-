package patient_management.pm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import patient_management.pm.entity.Patient;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByEmail(String email);
}
