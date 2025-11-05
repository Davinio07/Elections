package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.utils.xml.VotesTransformer;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchNationalVotesTransformer implements VotesTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the national level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchNationalVotesTransformer(Election election) {
        this.election = election;
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
        String seatsString = electionData.get("Seats");
        String percentageString = electionData.get("VotePercentage");

        int totalVotes = votesString != null ? Integer.parseInt(votesString) : 0;
        int seats = seatsString != null ? Integer.parseInt(seatsString) : 0;
        double votePercentage = percentageString != null ? Double.parseDouble(percentageString) : 0.0;

        NationalResult result = new NationalResult(partyName, totalVotes, seats, votePercentage);
        election.addNationalResult(result);

        System.out.printf("%s party votes: %s%n", aggregated ? "National" : "Constituency", electionData);
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
        System.out.printf("%s candidate votes: %s%n", aggregated ? "National" : "Constituency", electionData);
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
        System.out.printf("%s meta data: %s%n", aggregated ? "National" : "Constituency", electionData);
    }
}
