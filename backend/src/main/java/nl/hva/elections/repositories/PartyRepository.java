package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface PartyRepository extends JpaRepository<Party, Long> {
    /**
     * Finds a party entity by its registered name and election identifier.
     *
     * @param name       The registered name of the party.
     * @param electionId The identifier for the specific election (e.g., "TK2023").
     * @return an Optional containing the found party, or empty if not found.
     */
    Optional<Party> findByNameAndElectionId(String name, String electionId);

    /**
     * Finds all parties that participated in a specific election.
     *
     * @param electionId The identifier for the specific election (e.g., "TK2023").
     * @return A list of parties for the given election.
     */
    List<Party> findByElectionId(String electionId);

    Optional<Party> findById(int id);
    
}