package nl.hva.elections.repositories;

import nl.hva.elections.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Retrieves all messages, ordered by timestamp ascending.
     * This ensures the chat history is displayed chronologically.
     */
    List<ChatMessage> findAllByOrderByTimestampAsc();
}