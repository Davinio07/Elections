package nl.hva.elections.controllers;

import nl.hva.elections.dtos.ChatMessageDTO; // DTO voor WebSocket
import nl.hva.elections.repositories.ChatMessageRepository; // Repository
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ChatMessageRepository chatMessageRepository; // Repository

    // Injecteer de nieuwe Repository
    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Handelt inkomende berichten af, slaat ze op in de DB, en zendt ze uit.
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        String username = principal != null ? principal.getName() : "Anonymous";
        LocalDateTime now = LocalDateTime.now();

        // 1. Maak de JPA entiteit aan (gebruik de volledige naam om het DTO/Model conflict te vermijden)
        nl.hva.elections.models.ChatMessage jpaMessage = new nl.hva.elections.models.ChatMessage();
        jpaMessage.setSender(username);
        jpaMessage.setContent(chatMessageDTO.getContent());
        jpaMessage.setTimestamp(now);

        // 2. Sla het bericht op in de database
        chatMessageRepository.save(jpaMessage);

        // 3. Stuur de DTO met de juiste timestamp terug naar de clients
        chatMessageDTO.setSender(username);
        chatMessageDTO.setTimestamp(now.format(formatter));

        System.out.println("Message saved and sent from " + username);
        return chatMessageDTO;
    }
}