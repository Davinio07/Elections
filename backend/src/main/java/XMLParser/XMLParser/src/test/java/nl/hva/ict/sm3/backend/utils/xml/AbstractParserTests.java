package XMLParser.XMLParser.src.test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.BeforeEach;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractParserTests {
    protected TestTransformer transformer;

    protected DutchElectionParser electionProcessor;

    /**
     * This transformer add the election data on each method invocation to a list.
     */
    protected static class TestTransformer implements DefinitionTransformer, CandidateTransformer, VotesTransformer {
        protected final List<Map<String, String>> data = new LinkedList<>();
        protected int regionCalls;
        protected int partyCalls;
        protected int candidateCalls;
        protected int partyVoteCalls;
        protected int candidateVoteCalls;
        protected int metadataCalls;

        @Override
        public void registerRegion(Map<String, String> electionData) {
            data.add(Map.copyOf(electionData));
            regionCalls++;
        }

        @Override
        public void registerParty(Map<String, String> electionData) {
            data.add(Map.copyOf(electionData));
            partyCalls++;
        }

        @Override
        public void registerCandidate(Map<String, String> electionData) {
            data.add(Map.copyOf(electionData));
            candidateCalls++;
        }

        @Override
        public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
            Map<String, String> votesData = new HashMap<>(electionData);
            votesData.put("aggregated", Boolean.toString(aggregated));
            data.add(votesData);
            partyVoteCalls++;
        }

        @Override
        public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
            Map<String, String> votesData = new HashMap<>(electionData);
            votesData.put("aggregated", Boolean.toString(aggregated));
            data.add(votesData);
            candidateVoteCalls++;
        }

        @Override
        public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
            Map<String, String> votesData = new HashMap<>(electionData);
            votesData.put("aggregated", Boolean.toString(aggregated));
            data.add(votesData);
            metadataCalls++;
        }
    }

    @BeforeEach
    void setUp() {
        transformer = new TestTransformer();

        electionProcessor = new DutchElectionParser(
                transformer,
                transformer,
                transformer,
                transformer,
                transformer,
                transformer
        );
    }

    // Helper for debugging.
    protected void compareMaps(List<Map<String, String>> expectedList, List<Map<String, String>> actualList) {
        for (int i = 0; i < Math.min(expectedList.size(), actualList.size()); i++) {
            Map<String, String> expected = expectedList.get(i);
            Map<String, String> actual = actualList.get(i);

            Set<String> allKeys = new HashSet<>(expected.keySet());
            allKeys.addAll(actual.keySet());
            final int j = i;
            AtomicInteger valueDifferences = new AtomicInteger();
            allKeys.forEach(k -> {
                if (!Objects.equals(expected.get(k), actual.get(k))) {
                    valueDifferences.getAndIncrement();
                    System.out.printf("For %d the values for \"%s\" are different expecting \"%s\" but got \"%s\"\n", j, k, expected.get(k), actual.get(k));
                }
            });
            assertEquals(0, valueDifferences.get(), "There are values differences!");
        }
    }
}
