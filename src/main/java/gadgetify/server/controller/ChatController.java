package gadgetify.server.controller;

import gadgetify.server.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public Map<String, Object> sendMessageToGemini(@RequestBody Map<String, Object> payload) {
        return chatService.sendMessageToGemini(payload);
    }
}
