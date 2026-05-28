package ma.enset.digitalbanking.ai;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.ChatRequestDTO;
import ma.enset.digitalbanking.dtos.ChatResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final ChatbotService chatbotService;

    @Value("${app.telegram.bot-token:}")
    private String botToken;

    public void handleUpdate(Map<String, Object> update) {
        TelegramMessage message = extractMessage(update);
        if (message == null || !StringUtils.hasText(message.text())) {
            return;
        }

        ChatResponseDTO response = chatbotService.ask(ChatRequestDTO.builder()
                .message(message.text())
                .build());
        sendMessage(message.chatId(), response.getAnswer());
    }

    public boolean isEnabled() {
        return StringUtils.hasText(botToken);
    }

    @SuppressWarnings("unchecked")
    private TelegramMessage extractMessage(Map<String, Object> update) {
        Object messageObject = update.get("message");
        if (!(messageObject instanceof Map<?, ?> rawMessage)) {
            return null;
        }
        Map<String, Object> message = (Map<String, Object>) rawMessage;
        Object textObject = message.get("text");
        Object chatObject = message.get("chat");
        if (!(textObject instanceof String text) || !(chatObject instanceof Map<?, ?> rawChat)) {
            return null;
        }
        Map<String, Object> chat = (Map<String, Object>) rawChat;
        Object chatId = chat.get("id");
        return chatId == null ? null : new TelegramMessage(chatId.toString(), text);
    }

    private void sendMessage(String chatId, String text) {
        if (!isEnabled()) {
            return;
        }

        RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build()
                .post()
                .uri("/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "chat_id", chatId,
                        "text", text
                ))
                .retrieve()
                .toBodilessEntity();
    }

    private record TelegramMessage(String chatId, String text) {
    }
}
