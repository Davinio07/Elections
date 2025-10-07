package test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultParserTests extends AbstractParserTests {

    @Test
    void testResult2023() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        List<Map<String, String>> expectedDataList  = new LinkedList<>();
        createExpectedDataList(expectedDataList, "Resultaat_TK2023-Partial.eml.xml");

        electionProcessor.parseResults("TK2023", XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.PathUtils.getResourcePath("/%s".formatted("TK2023-Partial/Result")));

        compareMaps(expectedDataList, transformer.data);
        Set<Integer> usedResults = new HashSet<>();
        for (Map<String, String> data : transformer.data) {
            usedResults.add(expectedDataList.indexOf(data));
        }
        usedResults.remove(-1);
        assertEquals(expectedDataList.size(), usedResults.size());
        assertEquals(0, transformer.regionCalls, "VotesTransformer.registerRegion should NOT have been called!");
        assertEquals(0, transformer.partyCalls, "VotesTransformer.registerParty should NOT have been called!");
        assertEquals(0, transformer.candidateCalls, "VotesTransformer.registerCandidate should NOT have been called!");
        assertEquals(2, transformer.partyVoteCalls, "The calls to VotesTransformer.registerPartyVote don't match!");
        assertEquals(4, transformer.candidateVoteCalls, "The calls to VotesTransformer.registerCandidateVote don't match!");
        assertEquals(0, transformer.metadataCalls, "VotesTransformer.registerMeta should NOT have been called!");
    }

    private static void createExpectedDataList(List<Map<String, String>> expectedDataList, String fileName) {
        Map<String, String> baseData = new HashMap<>();
        baseData.put("fileName", fileName);
        baseData.put("EML-Id", "520");
        baseData.put("EML-SchemaVersion", "5");
        baseData.put("TransactionId", "1");
        baseData.put("AuthorityIdentifier-Id", "CSB");
        baseData.put("AuthorityIdentifier", "De Kiesraad");
        baseData.put("CreationDateTime", "2023-12-03T14:22:26.741");
        baseData.put("CanonicalizationMethod-Algorithm", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        baseData.put("ElectionIdentifier-Id", "TK2023");
        baseData.put("ElectionName", "Tweede Kamer der Staten-Generaal 2023");
        baseData.put("ElectionCategory", "TK");
        baseData.put("ElectionSubcategory", "TK");
        baseData.put("ElectionDate", "2023-11-22");
        baseData.put("ContestIdentifier-Id", "alle");
        baseData.put("aggregated", "true");

        // 0
        Map<String, String> partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("Elected", "yes");
        expectedDataList.add(Map.copyOf(partyData));
        // 1
        Map<String, String> candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("CandidateIdentifier-ShortCode", "YeşilgözD");
        candidateData.put("NameLine-NameType", "Initials");
        candidateData.put("NameLine", "D.");
        candidateData.put("FirstName", "Dilan");
        candidateData.put("LastName", "Yeşilgöz");
        candidateData.put("Gender", "female");
        candidateData.put("LocalityName", "Amsterdam");
        candidateData.put("Ranking", "1");
        candidateData.put("Elected", "yes");
        expectedDataList.add(Map.copyOf(candidateData));
        // 2
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("CandidateIdentifier-ShortCode", "HermansSTM");
        candidateData.put("NameLine", "S.T.M.");
        candidateData.put("FirstName", "Sophie");
        candidateData.put("LastName", "Hermans");
        candidateData.put("LocalityName", "Vreeland");
        expectedDataList.add(Map.copyOf(candidateData));
        // 3
        partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("Elected", "yes");
        expectedDataList.add(Map.copyOf(partyData));
        // 4
        candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("CandidateIdentifier-ShortCode", "JettenRAA");
        candidateData.put("NameLine-NameType", "Initials");
        candidateData.put("NameLine", "R.A.A.");
        candidateData.put("FirstName", "Rob");
        candidateData.put("LastName", "Jetten");
        candidateData.put("LocalityName", "Ubbergen");
        candidateData.put("Ranking", "1");
        candidateData.put("Elected", "yes");
        expectedDataList.add(Map.copyOf(candidateData));
        // 5
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("CandidateIdentifier-ShortCode", "PodtA");
        candidateData.put("NameLine", "A.");
        candidateData.put("FirstName", "Anne-Marijke");
        candidateData.put("LastName", "Podt");
        candidateData.put("LocalityName", "Utrecht");
        expectedDataList.add(Map.copyOf(candidateData));
    }

}
