package nl.hva.elections.services;

import nl.hva.elections.persistence.model.Party;
import nl.hva.elections.repositories.PartyRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class dbPartyService {

    private final PartyRepository partyRepository;

    public dbPartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public List<Party> getPartiesByElection(String electionId) {
        return partyRepository.findByElectionId(electionId);
    }
}
