//package com.clinichub.patient_service.service;
//
//import com.clinichub.patient_service.PatientDto;
//import com.clinichub.patient_service.model.Patient;
//import com.clinichub.patient_service.repo.PatientRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//
//// Specifies the use of Mockito extensions for JUnit 5
//@ExtendWith(MockitoExtension.class)
//class PatientServiceTest {
//
//    // Creates a mock object for the repository dependency
//    @Mock
//    PatientRepository repo;
//
//    // Injects the mocks (like 'repo') into the service object being tested
//    @InjectMocks
//    PatientService service;
//
//    @Test
//    void createsIdWhenMissing() {
//        // 1. Arrange (Setup)
//        // Create an input DTO with a null patientId
//        var dto = new PatientDto(null, "A", "B",
//                LocalDate.of(1990, 1, 1), "555-111", "SCHEDULED");
//
//        // Create a mock Patient entity that the repo would return after 'saving'
//        var saved = new Patient();
//        // Crucially, set the ID here to simulate the service generating and saving it
//        saved.setPatientId("generated-uuid-123");
//
//        // Configure the mock repository to return the 'saved' object
//        // whenever the 'save' method is called with ANY Patient object.
//        Mockito.when(repo.save(Mockito.any(Patient.class))).thenReturn(saved);
//
//        // 2. Act (Execution)
//        // Call the method being tested
//        var out = service.create(dto);
//
//        // 3. Assert (Verification)
//
//        // Verify that the output DTO has a non-null ID (meaning an ID was generated)
//        Assertions.assertNotNull(out.patientId());
//
//        // Verify that the repository's 'save' method was called exactly once
//        Mockito.verify(repo, Mockito.times(1)).save(Mockito.any(Patient.class));
//    }
//}