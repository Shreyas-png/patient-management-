package patient_management.pm.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patient_management.pm.dto.PatientRequestDTO;
import patient_management.pm.dto.PatientResponseDTO;
import patient_management.pm.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        List<PatientResponseDTO> allPatients = patientService.getAllPatients();
        return new ResponseEntity<>(allPatients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> savePatient(@Valid @RequestBody PatientRequestDTO newPatient) {
        PatientResponseDTO savedPatient = patientService.savePatient(newPatient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }
}
