package hr.algebra.jwdhealthcare.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.reminders.initial-delay-ms=3600000",
        "app.reports.scheduler.initial-delay-ms=3600000"
})
@AutoConfigureMockMvc
class ApiDataIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void patientTokenCanReadPatientAppointmentsAndReminders() throws Exception {
        String token = loginAndGetToken("patient", "password");

        mockMvc.perform(get("/api/patient/appointments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patient/reminders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void doctorTokenCanReadDoctorSchedule() throws Exception {
        String token = loginAndGetToken("doctor", "password");

        mockMvc.perform(get("/api/doctor/schedule")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminTokenCanReadUsersAndReports() throws Exception {
        String token = loginAndGetToken("admin", "password");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/reports")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractJsonStringValue(responseBody, "token");
    }

    private String extractJsonStringValue(String json, String propertyName) {
        String propertyPattern = "\"" + propertyName + "\"";
        int propertyIndex = json.indexOf(propertyPattern);

        if (propertyIndex < 0) {
            fail("JSON property was not found: " + propertyName);
        }

        int colonIndex = json.indexOf(":", propertyIndex + propertyPattern.length());
        int valueStartIndex = json.indexOf("\"", colonIndex + 1);
        int valueEndIndex = json.indexOf("\"", valueStartIndex + 1);

        if (colonIndex < 0 || valueStartIndex < 0 || valueEndIndex < 0) {
            fail("JSON string value could not be extracted for property: " + propertyName);
        }

        return json.substring(valueStartIndex + 1, valueEndIndex);
    }
}