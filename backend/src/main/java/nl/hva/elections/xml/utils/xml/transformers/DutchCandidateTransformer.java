package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.persistence.model.Election;
import nl.hva.elections.xml.utils.xml.CandidateTransformer;
import java.util.Map;

public class DutchCandidateTransformer implements CandidateTransformer {
    private final Election election;

    public DutchCandidateTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerCandidate(Map<String, String> electionData) {
        // 1. Extract all data into variables first
        String id = electionData.getOrDefault("CandidateIdentifier",
                electionData.getOrDefault("ContestIdentifier", ""));

        String firstName = electionData.get("FirstName");
        String lastName = electionData.get("LastName");
        String gender = electionData.get("Gender");
        String locality = electionData.get("LocalityName");

        // Party Name Logic
        String partyName = electionData.get("RegisteredAppellation");
        if (partyName == null || partyName.isBlank()) {
            partyName = electionData.get("RegisteredName");
        }

        // 2. Use the NEW constructor
        // We pass all variables at once. The constructor handles combining First+Last name.
        Candidate candidate = new Candidate(
                id,
                firstName,
                lastName,
                gender,
                locality,
                partyName
        );

        // 3. Log and Add
        if (partyName == null || partyName.isBlank()) {
            System.err.println("ERROR: Missing Party Name for candidate " + id);
        }

        election.addCandidate(candidate);

        // Note: candidate.getId() will be null here because the DB hasn't run yet.
        // Use getXmlId() to see the ID from the file.
        System.out.println("Registered candidate: " + candidate.getXmlId());
    }
}