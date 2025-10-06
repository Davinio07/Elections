package XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.xml;

import java.util.Map;

/**
 * A <code>VotesTransformer</code> is responsible for transforming the data into the appropriate model classes.
 */
public interface VotesTransformer {

    /**
     * Called whenever either a Selection closing tag is parsed and there is no CandidateIdentifier-Id or
     * CandidateIdentifier-ShortCode key is present.
     * @param aggregated <code>true</code> when the votes at the aggregation level are processed, <code>false</code>
     *                   otherwise.
     * @param electionData the tags and values.
     */
    void registerPartyVotes(boolean aggregated, Map<String, String> electionData);

    /**
     * Called whenever either a Selection closing tag is parsed and there is either a CandidateIdentifier-Id or
     * CandidateIdentifier-ShortCode key is present.
     * @param aggregated <code>true</code> when the votes at the aggregation level are processed, <code>false</code>
     *                   otherwise.
     * @param electionData the tags and values.
     */
    void registerCandidateVotes(boolean aggregated, Map<String, String> electionData);

    /**
     * Called whenever either a TotalVotes or ReportingUnitVotes closing tag is parsed.
     * @param aggregated <code>true</code> when the votes at the aggregation level are processed, <code>false</code>
     *                   otherwise.
     * @param electionData the tags and values.
     */
    void registerMetadata(boolean aggregated, Map<String, String> electionData);
}
