package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.utils.xml.VotesTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handles exclusively the processing of Constituency (Kieskring) aggregated votes.
 * Currently, we log them or store them if we extend the model later.
 * The calculation service currently aggregates municipalities, but this transformer
 * allows us to read the official totals from the XML if preferred.
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer {
    
    private static final Logger logger = LoggerFactory.getLogger(DutchConstituencyVotesTransformer.class);
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the constituency level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchConstituencyVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        // We ONLY process aggregated data here (Constituency totals)
        if (!aggregated) {
            return;
        }

        // For now, we can log it or add logic to store 'OfficialConstituencyResult'
        // Since our assignment focuses on aggregating municipalities, logging is safe for now.
        String partyName = electionData.get("RegisteredName");
        String votes = electionData.get("ValidVotes");
        // logger.debug("Constituency Total parsed: Party={} Votes={}", partyName, votes);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // Not needed
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // Not needed
    }
}
