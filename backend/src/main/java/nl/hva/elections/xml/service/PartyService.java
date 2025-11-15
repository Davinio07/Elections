package nl.hva.elections.xml.service;

import nl.hva.elections.exception.ElectionNotFoundException;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartyService {

    private static final Logger logger = LoggerFactory.getLogger(PartyService.class);

    private final DutchElectionService electionService;

    public PartyService(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Private helper to get the election data and throw a 404-style exception
     * if it's not found.
     *
     * @param electionId The identifier to check.
     * @return The non-null Election object.
     * @throws ElectionNotFoundException if the electionId is not found.
     */
    private Election getValidatedElectionData(String electionId) {

        Election data = electionService.getElectionData(electionId);

        if (data == null) {
            logger.warn("No election data found for id: {}", electionId);
            throw new ElectionNotFoundException("No election data found for id: " + electionId);
        }
        return data;
    }

    /**
     * Retrieves the list of PoliticalParty objects for a specific election.
     *
     * @param electionId The identifier of the election (e.g., "TK2023").
     * @return A list of PoliticalParty objects.
     * @throws ElectionNotFoundException if the electionId is not found.
     */
    public List<PoliticalParty> getPoliticalParties(String electionId) {
        // Now this method is safe. It will either return the list
        // or throw the correct ElectionNotFoundException.
        return getValidatedElectionData(electionId).getPoliticalParties();
    }

    /**
     * Finds a single political party by its name (case-insensitive search).
     *
     * @param electionId The identifier of the election.
     * @param partyName  The name to search for.
     * @return An Optional containing the found party, or empty if not found.
     * @throws ElectionNotFoundException if the electionId is not found.
     */
    public Optional<PoliticalParty> findPartyByName(String electionId, String partyName) {
        logger.info("Searching for party '{}' in election '{}'", partyName, electionId);

        // This call is now safe and will propagate the 404-exception if needed
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
     * @throws ElectionNotFoundException if the electionId is not found.
     */
    public int getPartyCount(String electionId) {
        logger.info("Counting parties for election: {}", electionId);
        // This call is now safe
        int count = getPoliticalParties(electionId).size();
        logger.info("Total parties for {}: {}", electionId, count);
        return count;
    }

    /**
     * Retrieves a list of just the names of all political parties.
     *
     * @param electionId The identifier of the election.
     * @return A list of party names.
     * @throws ElectionNotFoundException if the electionId is not found.
     */
    public List<String> getPartyNames(String electionId) {
        logger.info("Fetching party names for election: {}", electionId);
        // This call is now safe
        return getPoliticalParties(electionId).stream()
                .map(PoliticalParty::getRegisteredAppellation)
                .toList();
    }
}