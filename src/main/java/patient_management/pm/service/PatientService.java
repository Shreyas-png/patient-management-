package patient_management.pm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patient_management.pm.customexception.ResourceAlreadyExist;
import patient_management.pm.customexception.ResourceDoesNotExist;
import patient_management.pm.dto.PatientRequestDTO;
import patient_management.pm.dto.PatientResponseDTO;
import patient_management.pm.entity.Patient;
import patient_management.pm.repository.PatientRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDTO> allPatients = new ArrayList<>();
        for (Patient patient : patients) {
            PatientResponseDTO patientResponseDTO = new PatientResponseDTO(patient.getName(), patient.getEmail(), patient.getAddress(), patient.getDateOfBirth());
            allPatients.add(patientResponseDTO);
        }
        return allPatients;
    }

    public PatientResponseDTO savePatient(PatientRequestDTO newPatient) {
//        Optional<Patient> oldPatient = patientRepository.findByEmail(newPatient.getEmail());
//        if(oldPatient.isPresent()) throw new ResourceAlreadyExist("Patient with email " + newPatient.getEmail() + " exist");
        if (patientRepository.existsByEmail(newPatient.getEmail()))
            throw new ResourceAlreadyExist("Patient with email " + newPatient.getEmail() + " exist");
        Patient patient = new Patient(newPatient.getName(), newPatient.getEmail(), newPatient.getAddress(), newPatient.getDateOfBirth(), LocalDate.now());
        Patient savedPatient = patientRepository.save(patient);
        return new PatientResponseDTO(savedPatient.getName(), savedPatient.getEmail(), savedPatient.getAddress(), savedPatient.getDateOfBirth());
    }

    public PatientResponseDTO updatePatient(String email, PatientRequestDTO newPatient) {
        if (!patientRepository.existsByEmail(email))
            throw new ResourceDoesNotExist("Patient with email " + email + " does not exist");
        Patient oldPatient = patientRepository.findByEmail(email);
        if (newPatient.getName() != null && !newPatient.getName().isBlank()) oldPatient.setName(newPatient.getName());
        if (newPatient.getEmail() != null && !newPatient.getEmail().isBlank())
            oldPatient.setEmail(newPatient.getEmail());
        if (newPatient.getAddress() != null && !newPatient.getAddress().isBlank())
            oldPatient.setAddress(newPatient.getAddress());
        if (newPatient.getDateOfBirth() != null) oldPatient.setDateOfBirth(newPatient.getDateOfBirth());
        oldPatient = patientRepository.save(oldPatient);
        return new PatientResponseDTO(oldPatient.getName(), oldPatient.getEmail(), oldPatient.getAddress(), oldPatient.getDateOfBirth());
    }

    @Transactional
    public void deletePatient(String email){
        if(!patientRepository.existsByEmail(email)) throw new ResourceDoesNotExist("Patient with email " + email + " does not exist");
        patientRepository.deleteByEmail(email);
    }
}
