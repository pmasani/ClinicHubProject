//@org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest(com.clinichub.patient_service.PatientController.class)
//class PatientControllerTest {
//    @org.springframework.beans.factory.annotation.Autowired private org.springframework.test.web.servlet.MockMvc mvc;
//    @org.springframework.boot.test.mock.mockito.MockBean private com.clinichub.patient_service.service.PatientService service;
//
//    @org.junit.jupiter.api.Test
//    void createReturns201() throws Exception {
//        var resp = new com.clinichub.patient_service.PatientDto("id-1","A","B",
//                java.time.LocalDate.of(1990,1,1),"555","SCHEDULED");
//        org.mockito.Mockito.when(service.create(org.mockito.Mockito.any())).thenReturn(resp);
//
//        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/patients")
//                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
//                        .content("""
//          {"firstName":"A","lastName":"B","dob":"1990-01-01","phone":"555","status":"SCHEDULED"}
//        """))
//                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
//                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.patientId").value("id-1"));
//    }
//}
