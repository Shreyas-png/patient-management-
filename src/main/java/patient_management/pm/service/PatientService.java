package patient_management.pm.service;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
                .setStatus("ACTIVE")
                .build();

        //Calling GRPC Client method to add patient to billing service
        BillingResponse billingResponse = billingServiceGrpcClient.addPatientToBillingService(billingRequest);
        log.info(billingResponse.getPatientId() + " " + billingResponse.getEmail());

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

        //Checking if patient exists
        if (!patientRepository.existsByEmail(email))
            throw new ResourceDoesNotExist("Patient with email " + email + " does not exist");
        Patient oldPatient = patientRepository.findByEmail(email);

        //Updating patient's name
        if (newPatient.getName() != null && !newPatient.getName().isBlank())
            oldPatient.setName(newPatient.getName());

        //Updating patient's email
        if (newPatient.getEmail() != null && !newPatient.getEmail().isBlank())
            oldPatient.setEmail(newPatient.getEmail());

        //Updating patient's address
        if (newPatient.getAddress() != null && !newPatient.getAddress().isBlank())
            oldPatient.setAddress(newPatient.getAddress());

        //Updating patient's DOB
        if (newPatient.getDateOfBirth() != null) oldPatient.setDateOfBirth(newPatient.getDateOfBirth());
        oldPatient = patientRepository.save(oldPatient);

        //Building GRPC Request for billing account
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setEmail(oldPatient.getEmail())
                .setPatientId(oldPatient.getId().toString())
                .setStatus("ACTIVE")
                .build();

        //Calling GRPC Client method to add patient to billing service
        BillingResponse billingResponse = billingServiceGrpcClient.addPatientToBillingService(billingRequest);
        log.info(billingResponse.getPatientId() + " " + billingResponse.getEmail());

        //Building patient response dto and returning the same
        return PatientResponseDTO.builder()
                .name(oldPatient.getName())
                .email(oldPatient.getEmail())
                .address(oldPatient.getAddress())
                .dateOfBirth(oldPatient.getDateOfBirth())
                .build();
    }

    @Transactional
    public void deletePatient(String email){

        //checking if patient exists
        if(!patientRepository.existsByEmail(email))
            throw new ResourceDoesNotExist("Patient with email " + email + " does not exist");

        //getting patient details to make rpc call
        Patient oldPatient = patientRepository.findByEmail(email);

        //deleting the patient
        patientRepository.deleteByEmail(email);

        //Building GRPC Request for billing account
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setEmail(oldPatient.getEmail())
                .setPatientId(oldPatient.getId().toString())
                .setStatus("INACTIVE")
                .build();

        //Calling GRPC Client method to add patient to billing service
        BillingResponse billingResponse = billingServiceGrpcClient.addPatientToBillingService(billingRequest);
        log.info(billingResponse.getPatientId() + " " + billingResponse.getEmail());
    }
}
