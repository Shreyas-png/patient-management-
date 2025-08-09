package patient_management.pm.service;

import org.springframework.stereotype.Service;
import patient_management.pm.customexception.ResourceAlreadyExist;
import patient_management.pm.dto.PatientRequestDTO;
import patient_management.pm.dto.PatientResponseDTO;
import patient_management.pm.entity.Patient;
import patient_management.pm.repository.PatientRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDTO> allPatients = new ArrayList<>();
        for(Patient patient : patients){
            PatientResponseDTO patientResponseDTO = new PatientResponseDTO(patient.getName(), patient.getEmail(), patient.getAddress(), patient.getDateOfBirth());
            allPatients.add(patientResponseDTO);
        }
        return allPatients;
    }

    public PatientResponseDTO savePatient(PatientRequestDTO newPatient){
        Optional<Patient> oldPatient = patientRepository.findByEmail(newPatient.getEmail());
        if(oldPatient.isPresent()) throw new ResourceAlreadyExist("Patient with email " + newPatient.getEmail() + " exist");
        Patient patient = new Patient(newPatient.getName(), newPatient.getEmail(), newPatient.getAddress(), newPatient.getDateOfBirth(), LocalDate.now());
        Patient savedPatient = patientRepository.save(patient);
        return new PatientResponseDTO(savedPatient.getName(), savedPatient.getEmail(), savedPatient.getAddress(), savedPatient.getDateOfBirth());
    }
}
