package XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.xml.transformers;

import XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.model.Election;
import XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.xml.VotesTransformer;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer {
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
        System.out.printf("%s party votes: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s candidate votes: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }
}
