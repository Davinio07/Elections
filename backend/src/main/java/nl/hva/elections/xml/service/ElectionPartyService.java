package nl.hva.elections.xml.service;

import nl.hva.elections.xml.model.PoliticalParty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for all business logic related to Political Parties
 * retrieved from the cached XML data.
 */
@Service
public class PartyService {

    private static final Logger logger = LoggerFactory.getLogger(PartyService.class);

    /**
     * The service that holds the cached, pre-parsed XML data.
     */
    private final DutchElectionService electionService;

    public PartyService(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Retrieves the list of PoliticalParty objects for a specific election.
     *
     * @param electionId The identifier of the election (e.g., "TK2023").
     * @return A list of PoliticalParty objects.
     */
    public List<PoliticalParty> getPoliticalParties(String electionId) {
        // Haalt de data op uit de hoofd-service cache
        return electionService.getElectionData(electionId).getPoliticalParties();
    }

    /**
     * Finds a single political party by its name (case-insensitive search).
     *
     * @param electionId The identifier of the election.
     * @param partyName  The name to search for.
     * @return An Optional containing the found party, or empty if not found.
     */
    public Optional<PoliticalParty> findPartyByName(String electionId, String partyName) {
        logger.info("Searching for party '{}' in election '{}'", partyName, electionId);

        // Gebruikt de getPoliticalParties methode van deze service
        return getPoliticalParties(electionId).stream()
                .filter(party -> party.getRegisteredAppellation()
                        .toLowerCase()
                        .contains(partyName.toLowerCase()))
                .findFirst();
    }

    /**
     * Counts the total number of political parties for an election.
     *
     * @param electionId The identifier of the election.
     * @return The total count of parties.
     */
    public int getPartyCount(String electionId) {
        logger.info("Counting parties for election: {}", electionId);
        int count = getPoliticalParties(electionId).size();
        logger.info("Total parties for {}: {}", electionId, count);
        return count;
    }

    /**
     * Retrieves a list of just the names of all political parties.
     *
     * @param electionId The identifier of the election.
     * @return A list of party names.
     */
    public List<String> getPartyNames(String electionId) {
        logger.info("Fetching party names for election: {}", electionId);
        return getPoliticalParties(electionId).stream()
                .map(PoliticalParty::getRegisteredAppellation)
                .toList();
    }
}