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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.reminders.initial-delay-ms=3600000",
        "app.reports.scheduler.initial-delay-ms=3600000"
})
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithValidAdminCredentialsReturnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    void currentUserWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminTokenCanAccessAdminStatus() throws Exception {
        String token = loginAndGetToken("admin", "password");

        mockMvc.perform(get("/api/admin/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.area").value("admin"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void patientTokenCannotAccessAdminStatus() throws Exception {
        String token = loginAndGetToken("patient", "password");

        mockMvc.perform(get("/api/admin/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
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