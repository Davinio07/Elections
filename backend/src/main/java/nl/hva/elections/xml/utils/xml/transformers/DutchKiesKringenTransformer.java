package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.KiesKring;
import nl.hva.elections.xml.utils.xml.VotesTransformer;

import java.util.Map;

/**
 * This class handles municipality-level votes from XML files.
 * It takes the data and puts it into our data model.
 */
public class DutchKiesKringenTransformer implements VotesTransformer {
    private final Election election;

    /**
     * Creates a new transformer. It needs an 'Election' object to store the votes in.
     * @param election The main election object where we store all results.
     */
    public DutchKiesKringenTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        // We only want non-aggregated data, which is the municipality data.
        // The aggregated data is for national or constituency votes.
        if (aggregated) {
            return;
        }

        // Get the information we need from the electionData map
        String municipalityName = electionData.get("ReportingUnitIdentifier");
        String partyName = electionData.get("RegisteredName");
        String votesString = electionData.get("ValidVotes");

        // Make sure we have the info we need before we try to save it
        if (municipalityName != null && !municipalityName.isBlank() && partyName != null) {
            // Convert the votes from a string to a number. If it's not a number, use 0.
            int totalVotes = 0;
            if (votesString != null) {
                totalVotes = Integer.parseInt(votesString);
            }

            // Create a new MunicipalityResult object with the data
            KiesKring result = new KiesKring(municipalityName, partyName, totalVotes);
            // Add the new result to our main Election object
            election.addMunicipalityResult(result);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        // This is not needed for this feature
    }
    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // This is not needed for this feature
    }
}