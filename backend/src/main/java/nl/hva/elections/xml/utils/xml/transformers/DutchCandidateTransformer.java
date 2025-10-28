package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Candidate;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.utils.xml.CandidateTransformer;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchCandidateTransformer implements CandidateTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the candidate lists. It expects an instance of Election that can
     * be used for storing the candidates lists.
     * @param election the election in which the candidate lists wil be stored.
     */
    public DutchCandidateTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerCandidate(Map<String, String> electionData) {
        // Prefer CandidateIdentifier if present; fall back to ContestIdentifier
        String id = electionData.getOrDefault("CandidateIdentifier",
                electionData.getOrDefault("ContestIdentifier", ""));

        Candidate candidate = new Candidate(id);
        candidate.setFirstName(electionData.get("FirstName"));
        candidate.setLastName(electionData.get("LastName"));
        candidate.setGender(electionData.get("Gender"));
        candidate.setLocality(electionData.get("LocalityName"));

        // --- FIX: Check for both RegisteredAppellation and RegisteredName ---
        String partyName = electionData.get("RegisteredAppellation");
        if (partyName == null || partyName.isBlank()) {
            // Fallback to the other potential tag for party name
            partyName = electionData.get("RegisteredName");
        }
        // -------------------------------------------------------------------

        // Diagnostic output (keep this to ensure data is found now)
        if (partyName == null || partyName.isBlank()) {
            System.err.println("ERROR: Missing RegisteredAppellation/RegisteredName for candidate " + id + ".");
        } else {
            System.out.println("Found Party Name for candidate " + id + ": [" + partyName + "]");
        }

        candidate.setPartyName(partyName);
        election.addCandidate(candidate);

        System.out.println("Registered candidate: " + candidate.getId());
    }

}
