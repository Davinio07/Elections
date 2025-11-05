package nl.hva.elections.xml.utils.xml;

import java.util.Map;

/**
 * A <code>CandidateTransformer</code> is responsible for transforming the data into the appropriate model classes.
 */
public interface CandidateTransformer {

    /**
     * Called whenever a Candidate closing tag is parsed.
     * @param electionData the tags and values.
     */
    void registerCandidate(Map<String, String> electionData);

}
