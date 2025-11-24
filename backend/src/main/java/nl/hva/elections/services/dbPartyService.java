package nl.hva.elections.services;

import nl.hva.elections.xml.model.Party;
import nl.hva.elections.repositories.PartyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class dbPartyService {

    private final PartyRepository partyRepository;

    public dbPartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    /**
     * Retrieves all parties participating in a specific election from the database.
     */
    public List<Party> getPartiesByElection(String electionId) {
        return partyRepository.findByElectionId(electionId);
    }

    /**
     * Finds a specific party by name and election ID in the database.
     */
    public Optional<Party> findPartyByName(String electionId, String name) {
        return partyRepository.findByNameAndElectionId(name, electionId);
    }
}