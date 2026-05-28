package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.ai.ChatbotService;
import ma.enset.digitalbanking.dtos.ChatRequestDTO;
import ma.enset.digitalbanking.dtos.ChatResponseDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ChatResponseDTO ask(@RequestBody ChatRequestDTO requestDTO) {
        return chatbotService.ask(requestDTO);
    }

    @GetMapping("/history")
    public List<ChatResponseDTO> history() {
        return chatbotService.history();
    }
}
