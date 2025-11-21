package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.Party;
import nl.hva.elections.xml.utils.xml.VotesTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchNationalVotesTransformer implements VotesTransformer {
    private static final Logger log = LoggerFactory.getLogger(DutchNationalVotesTransformer.class);

    private final Election election;
    private final PartyRepository partyRepository;

    /**
     * Creates a new transformer for handling the votes at the national level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchNationalVotesTransformer(Election election, PartyRepository partyRepository) {
        this.election = election;
        this.partyRepository = partyRepository;
    }

    /**
     * Registers the number of valid votes for a political party at the national level.
     * The method extracts the party name and vote count from the provided election data,
     * stores them in the associated {@link Election}, and prints the results.
     *
     * @param aggregated   {@code true} if the data represents aggregated national results;
     *                     {@code false} if it represents constituency-level data
     * @param electionData a map containing election data fields such as "RegisteredName" and "ValidVotes"
     */
    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        String partyName = electionData.get("RegisteredName");
        String votesString = electionData.get("ValidVotes");

        int totalVotes = votesString != null ? Integer.parseInt(votesString) : 0;

        // Election ID comes from the Election object
        String electionId = election.getId();

        // 1. Lookup the party (correct repository method)
        Party party = partyRepository
                .findByNameAndElectionId(partyName, electionId)
                .orElseGet(() -> {
                    log.info("Party '{}' not found for election '{}'. Creating new entity.", partyName, electionId);
                    Party p = new Party();
                    p.setName(partyName);
                    p.setElectionId(electionId);
                    return partyRepository.save(p);
                });

        // 2. Update the vote count
        party.setVotes(totalVotes);

        // 3. Persist changes
        partyRepository.save(party);

        // 4. Link the party to the in-memory Election object
        election.addNationalResult(party);
    }


    /**
     * Registers candidate-level vote information.
     * This implementation currently only prints the received data to the standard output.
     *
     * @param aggregated   {@code true} if the data represents aggregated national results;
     *                     {@code false} if it represents constituency-level data
     * @param electionData a map containing candidate-related election data
     */
    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // System.out.printf("%s candidate votes: %s%n", aggregated ? "National" : "Constituency", electionData);
    }

    /**
     * Registers metadata related to the election.
     * This implementation currently only prints the received metadata to the standard output.
     *
     * @param aggregated   {@code true} if the data represents aggregated national results;
     *                     {@code false} if it represents constituency-level data
     * @param electionData a map containing metadata fields for the election
     */
    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // System.out.printf("%s meta data: %s%n", aggregated ? "National" : "Constituency", electionData);
    }
}
