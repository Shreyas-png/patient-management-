package patient_management.pm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patient_management.pm.customexception.ResourceAlreadyExist;
import patient_management.pm.customexception.ResourceDoesNotExist;
import patient_management.pm.dto.PatientRequestDTO;
import patient_management.pm.dto.PatientResponseDTO;
import patient_management.pm.entity.Patient;
import patient_management.pm.grpc.BillingServiceGrpcClient;
import patient_management.pm.proto.BillingRequest;
import patient_management.pm.proto.BillingResponse;
import patient_management.pm.repository.PatientRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
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

    @Transactional
    public PatientResponseDTO savePatient(PatientRequestDTO newPatient) {

        //Checking for duplicate entry
        if (patientRepository.existsByEmail(newPatient.getEmail()))
            throw new ResourceAlreadyExist("Patient with email " + newPatient.getEmail() + " exist");

        //Building an entity
        Patient patient = Patient.builder()
                .name(newPatient.getName())
                .address(newPatient.getAddress())
                .email(newPatient.getEmail())
                .registeredDate(LocalDate.now())
                .dateOfBirth(newPatient.getDateOfBirth())
                .build();

        //Adding the patient to patient db
        Patient savedPatient = patientRepository.save(patient);

        //Building GRPC Request for billing account
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setEmail(savedPatient.getEmail())
                .setPatientId(savedPatient.getId().toString())
                .setName(savedPatient.getName())
                .build();

        //Calling GRPC Client method to add patient to billing service
        BillingResponse billingResponse = billingServiceGrpcClient.addPatientToBillingService(billingRequest);

        //Building patient response DTO and returning the same
        return  PatientResponseDTO.builder()
                .name(savedPatient.getName())
                .address(savedPatient.getAddress())
                .dateOfBirth(savedPatient.getDateOfBirth())
                .email(savedPatient.getEmail())
                .build();
    }

    @Transactional
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
