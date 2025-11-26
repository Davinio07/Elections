package nl.hva.elections.controllers;

import nl.hva.elections.dtos.ChatMessageDTO; // DTO voor REST Response
import nl.hva.elections.repositories.ChatMessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/chat") // Dit is de REST API route
public class ChatHistoryController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ChatMessageRepository chatMessageRepository;

    public ChatHistoryController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * REST endpoint om de chat historie op te halen.
     * Endpoint: GET /api/chat/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory() {
        // Haalt alle berichten op, gesorteerd op tijd
        // We gebruiken hier de volledige naam van het JPA Model om verwarring met de DTO te voorkomen.
        List<nl.hva.elections.models.ChatMessage> jpaMessages = chatMessageRepository.findAllByOrderByTimestampAsc();

        // Converteer JPA entiteiten naar DTO's
        List<ChatMessageDTO> dtos = jpaMessages.stream()
                .map(msg -> {
                    // Gebruikt de geimporteerde DTO nl.hva.elections.dtos.ChatMessage
                    ChatMessageDTO dto = new ChatMessageDTO();
                    dto.setSender(msg.getSender());
                    dto.setContent(msg.getContent());
                    // Formatteer de LocalDateTime naar de string die de frontend verwacht
                    dto.setTimestamp(msg.getTimestamp().format(formatter));
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }
}