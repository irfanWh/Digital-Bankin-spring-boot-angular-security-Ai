package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.ai.TelegramBotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramBotService telegramBotService;

    @Value("${app.telegram.webhook-secret:}")
    private String webhookSecret;

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webhook(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String providedSecret,
            @RequestBody Map<String, Object> update
    ) {
        if (!StringUtils.hasText(webhookSecret) || !webhookSecret.equals(providedSecret)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Telegram webhook secret");
        }
        if (telegramBotService.isEnabled()) {
            telegramBotService.handleUpdate(update);
        }
    }
}
