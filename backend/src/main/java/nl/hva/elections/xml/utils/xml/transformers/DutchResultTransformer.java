package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.utils.xml.VotesTransformer;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchResultTransformer implements VotesTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the results. It expects an instance of
     * Election that can be used for storing the results. The results contain either which party has candidates who
     * have been elected or the candidates who are elected.
     * @param election the election in which the votes wil be stored.
     */
    public DutchResultTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("National party result: %s\n", electionData);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("National candidate result: %s\n", electionData);
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // throw new IllegalStateException("There is no implementation on purpose.");
    }
}
