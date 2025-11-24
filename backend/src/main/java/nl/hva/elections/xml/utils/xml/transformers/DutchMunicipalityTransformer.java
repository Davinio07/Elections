package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.KiesKring;
import nl.hva.elections.xml.utils.xml.VotesTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This transformer handles the results per Municipality (Gemeente).
 * It reads the data from the "ReportingUnit" sections in the XML.
 */
public class DutchMunicipalityTransformer implements VotesTransformer {
    
    private static final Logger logger = LoggerFactory.getLogger(DutchMunicipalityTransformer.class);
    private final Election election;

    public DutchMunicipalityTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        // IMPORTANT: We only want the non-aggregated data here (the municipalities).
        // The aggregated data (constituency totals) is handled by the ConstituencyTransformer.
        if (aggregated) {
            return;
        }

        try {
            String municipalityName = electionData.get("ReportingUnitIdentifier");
            String partyName = electionData.get("RegisteredName");
            String votesString = electionData.get("ValidVotes");

            if (municipalityName != null && partyName != null && votesString != null) {
                int validVotes = Integer.parseInt(votesString);

                // We reuse the KiesKring model to store municipality results for now.
                KiesKring result = new KiesKring(municipalityName, partyName, validVotes);
                election.addMunicipalityResult(result);
            }
        } catch (NumberFormatException e) {
            logger.warn("Could not parse votes for municipality data: {}", electionData);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // We do not need candidate results per municipality for this feature.
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // Not needed.
    }
}