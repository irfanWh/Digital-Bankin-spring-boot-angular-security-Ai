package ma.enset.digitalbanking.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.audit.AuditService;
import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.ChatRequestDTO;
import ma.enset.digitalbanking.dtos.ChatResponseDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.DashboardStatsDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final BankAccountService bankAccountService;
    private final AuditService auditService;
    private final ResourcePatternResolver resourcePatternResolver;
    private final ObjectMapper objectMapper;

    private final Map<String, List<ChatResponseDTO>> history = new ConcurrentHashMap<>();

    @Value("${app.ai.openai.api-key:}")
    private String openAiApiKey;

    @Value("${app.ai.openai.model:gpt-5.2}")
    private String openAiModel;

    public ChatResponseDTO ask(ChatRequestDTO requestDTO) {
        String username = auditService.currentUsername();
        String context = buildContext(username);
        String prompt = buildPrompt(requestDTO.getMessage(), context);
        String answer = StringUtils.hasText(openAiApiKey)
                ? askOpenAi(prompt)
                : localFallbackAnswer(requestDTO.getMessage(), context);

        ChatResponseDTO responseDTO = ChatResponseDTO.builder()
                .answer(answer)
                .createdAt(LocalDateTime.now())
                .build();
        history.computeIfAbsent(username, key -> new ArrayList<>()).add(responseDTO);
        return responseDTO;
    }

    public List<ChatResponseDTO> history() {
        return history.getOrDefault(auditService.currentUsername(), List.of());
    }

    private String askOpenAi(String prompt) {
        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl("https://api.openai.com/v1")
                    .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                    .build();

            String response = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", openAiModel,
                            "input", prompt
                    ))
                    .retrieve()
                    .body(String.class);

            return extractOutputText(response);
        } catch (RestClientException | IOException | IllegalArgumentException exception) {
            return "Le service IA est momentanément indisponible. Voici le contexte local disponible :\n\n"
                    + summarizeContext(prompt);
        }
    }

    private String extractOutputText(String response) throws IOException {
        if (!StringUtils.hasText(response)) {
            return "Le service IA n'a retourné aucun contenu.";
        }
        JsonNode root = objectMapper.readTree(response);
        JsonNode outputText = root.get("output_text");
        if (outputText != null && outputText.isTextual()) {
            return outputText.asText();
        }

        JsonNode output = root.get("output");
        if (output != null && output.isArray()) {
            StringBuilder builder = new StringBuilder();
            output.forEach(item -> {
                JsonNode content = item.get("content");
                if (content != null && content.isArray()) {
                    content.forEach(contentItem -> {
                        JsonNode text = contentItem.get("text");
                        if (text != null && text.isTextual()) {
                            builder.append(text.asText()).append('\n');
                        }
                    });
                }
            });
            if (!builder.isEmpty()) {
                return builder.toString().trim();
            }
        }

        return "Je n'ai pas pu extraire une réponse lisible du service IA.";
    }

    private String buildContext(String username) {
        DashboardStatsDTO stats = bankAccountService.dashboardStats();
        List<CustomerDTO> customers = bankAccountService.listCustomers();
        List<BankAccountDTO> accounts = bankAccountService.bankAccountList();

        return """
                Utilisateur authentifié: %s

                Base de connaissances:
                %s

                Statistiques:
                - Total clients: %d
                - Total comptes: %d
                - Solde total: %.2f
                - Opérations par type: %s
                - Comptes par statut: %s
                - Comptes par type: %s

                Clients visibles:
                %s

                Comptes visibles:
                %s
                """.formatted(
                username,
                loadKnowledgeBase(),
                stats.getTotalCustomers(),
                stats.getTotalAccounts(),
                stats.getTotalBalance(),
                stats.getOperationsByType(),
                stats.getAccountsByStatus(),
                stats.getAccountsByType(),
                customers.stream().map(customer -> customer.getId() + " - " + customer.getName()).toList(),
                accounts.stream().map(account -> account.getId() + " - " + account.getType() + " - " + account.getBalance()).toList()
        );
    }

    private String buildPrompt(String message, String context) {
        return """
                Tu es le chatbot sécurisé d'une application Digital Banking.
                Réponds en français, simplement, et n'invente pas de données.
                Utilise uniquement le contexte fourni. Si une information manque, dis-le.
                Ne retourne jamais de clé API, secret JWT ou token.

                Contexte:
                %s

                Question:
                %s
                """.formatted(context, message);
    }

    private String loadKnowledgeBase() {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:/rag/*.md");
            StringBuilder builder = new StringBuilder();
            for (Resource resource : resources) {
                builder.append(resource.getContentAsString(StandardCharsets.UTF_8)).append("\n\n");
            }
            return builder.toString();
        } catch (IOException exception) {
            return "Base de connaissances indisponible.";
        }
    }

    private String localFallbackAnswer(String message, String context) {
        return """
                Le chatbot local est actif, mais OPENAI_API_KEY n'est pas configurée.
                Question reçue: %s

                Résumé du contexte disponible:
                %s
                """.formatted(message, summarizeContext(context));
    }

    private String summarizeContext(String context) {
        int maxLength = Math.min(context.length(), 1200);
        return context.substring(0, maxLength);
    }
}
