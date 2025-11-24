package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.MunicipalityResult;
import nl.hva.elections.xml.utils.xml.VotesTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handles exclusively the processing of Municipality (ReportingUnit) votes.
 * Creates MunicipalityResult objects for the Election model.
 */
public class DutchMunicipalityTransformer implements VotesTransformer {
    
    private static final Logger logger = LoggerFactory.getLogger(DutchMunicipalityTransformer.class);
    private final Election election;

    public DutchMunicipalityTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        // We SKIP aggregated data here. That is for the ConstituencyTransformer.
        if (aggregated) {
            return;
        }

        try {
            String municipalityName = electionData.get("ReportingUnitIdentifier");
            String partyName = electionData.get("RegisteredName");
            String votesString = electionData.get("ValidVotes");

            if (municipalityName != null && partyName != null && votesString != null) {
                int validVotes = Integer.parseInt(votesString);
                MunicipalityResult result = new MunicipalityResult(municipalityName, partyName, validVotes);
                election.addMunicipalityResult(result);
            }
        } catch (NumberFormatException e) {
            logger.warn("Could not parse votes for municipality data: {}", electionData);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // Not needed for this feature
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // Not needed
    }
}