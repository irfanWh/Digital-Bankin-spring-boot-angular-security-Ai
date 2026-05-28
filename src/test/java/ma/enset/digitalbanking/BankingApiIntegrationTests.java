package ma.enset.digitalbanking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankingApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSeedCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldCreateCustomerAndSearchByName() throws Exception {
        String customerPayload = """
                {
                  "name": "Test Customer",
                  "email": "test.customer@example.com"
                }
                """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Customer"));

        mockMvc.perform(get("/api/customers/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldCreateCurrentAccountAndCreditIt() throws Exception {
        String customerPayload = """
                {
                  "name": "Account Owner",
                  "email": "account.owner@example.com"
                }
                """;

        String customerResponse = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerPayload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode customerJson = objectMapper.readTree(customerResponse);
        long customerId = customerJson.get("id").asLong();

        String accountPayload = """
                {
                  "initialBalance": 1000,
                  "overDraft": 500,
                  "customerId": %d
                }
                """.formatted(customerId);

        String accountResponse = mockMvc.perform(post("/api/accounts/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.balance").value(1000.0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode accountJson = objectMapper.readTree(accountResponse);
        String accountId = accountJson.get("id").asText();

        String creditPayload = """
                {
                  "accountId": "%s",
                  "amount": 250,
                  "description": "REST test credit"
                }
                """.formatted(accountId);

        mockMvc.perform(post("/api/accounts/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creditPayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1250.0));
    }
}
