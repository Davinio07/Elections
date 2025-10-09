package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.utils.xml.VotesTransformer;
import java.util.Map;

/**
 * This special transformer can send data to two other transformers at the same time.
 * This is useful if a single file needs to be processed by multiple transformers.
 */
public class CompositeVotesTransformer implements VotesTransformer {

    private final VotesTransformer primaryTransformer;
    private final VotesTransformer secondaryTransformer;

    public CompositeVotesTransformer(VotesTransformer primary, VotesTransformer secondary) {
        this.primaryTransformer = primary;
        this.secondaryTransformer = secondary;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        primaryTransformer.registerPartyVotes(aggregated, electionData);
        secondaryTransformer.registerPartyVotes(aggregated, electionData);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        primaryTransformer.registerCandidateVotes(aggregated, electionData);
        secondaryTransformer.registerCandidateVotes(aggregated, electionData);
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        primaryTransformer.registerMetadata(aggregated, electionData);
        secondaryTransformer.registerMetadata(aggregated, electionData);
    }
}