package nl.hva.elections.controllers;

import nl.hva.elections.dtos.ChatMessage;
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

    /**
     * Handles incoming messages sent to /app/chat.send.
     * The message is then sent to all subscribers of /topic/public.
     * * @param chatMessage The message content from the client.
     * @param principal The authenticated user (inferred from the JWT token).
     * @return The message to be broadcasted.
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // Principal is null if the user is not authenticated.
        String username = principal != null ? principal.getName() : "Anonymous";

        // Set server-side data before broadcasting
        chatMessage.setSender(username);
        chatMessage.setTimestamp(LocalDateTime.now().format(formatter));

        System.out.println("Message received from " + username + ": " + chatMessage.getContent());
        return chatMessage;
    }
}