package nl.hva.elections.xml.utils.xml;

import java.util.Map;

public interface CandidateTransformer {

    void registerCandidate(Map<String, String> electionData);

}
