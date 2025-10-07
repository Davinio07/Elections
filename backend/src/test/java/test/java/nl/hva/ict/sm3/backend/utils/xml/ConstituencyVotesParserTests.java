package test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstituencyVotesParserTests extends AbstractParserTests {

    @Test
    void testTotal2023() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        List<Map<String, String>> expectedDataList  = new LinkedList<>();
        createExpectedDataList(expectedDataList, "Telling_TK2023_kieskring_Haarlem-Partial-Copy.eml.xml");
        createExpectedDataList(expectedDataList, "Telling_TK2023_kieskring_Haarlem-Partial.eml.xml");

        electionProcessor.parseResults("TK2023", XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.PathUtils.getResourcePath("/%s".formatted("TK2023-Partial/Constituency")));

        compareMaps(expectedDataList, transformer.data);
        Set<Integer> usedResults = new HashSet<>();
        for (Map<String, String> data : transformer.data) {
            usedResults.add(expectedDataList.indexOf(data));
        }
        usedResults.remove(-1);
        assertEquals(expectedDataList.size(), usedResults.size());
        assertEquals(0, transformer.regionCalls, "VotesTransformer.registerRegion should NOT have been called!");
        assertEquals(0, transformer.partyCalls, "VotesTransformer.registerParty should NOT have been called!");
        assertEquals(0, transformer.candidateCalls, "The calls to VotesTransformer.registerCandidate don't match!");
        assertEquals(12, transformer.partyVoteCalls, "The calls to VotesTransformer.registerPartyVote don't match!");
        assertEquals(24, transformer.candidateVoteCalls, "The calls to VotesTransformer.registerCandidateVote don't match!");
        assertEquals(6, transformer.metadataCalls, "The calls to VotesTransformer.registerMeta don't match!");
    }

    private static void createExpectedDataList(List<Map<String, String>> expectedDataList, String fileName) {
        Map<String, String> baseData = new HashMap<>();
        baseData.put("fileName", fileName);
        baseData.put("EML-Id", "510c");
        baseData.put("EML-SchemaVersion", "5");
        baseData.put("TransactionId", "1");
        baseData.put("AuthorityIdentifier-Id", "HSB10");
        baseData.put("AuthorityIdentifier", "Haarlem");
        baseData.put("CreationDateTime", "2023-11-30T16:47:36.336");
        baseData.put("CanonicalizationMethod-Algorithm", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        baseData.put("ElectionIdentifier-Id", "TK2023");
        baseData.put("ElectionName", "Tweede Kamer der Staten-Generaal 2023");
        baseData.put("ElectionCategory", "TK");
        baseData.put("ElectionSubcategory", "TK");
        baseData.put("ElectionDate", "2023-11-22");
        baseData.put("ContestIdentifier-Id", "10");
        baseData.put("ContestName", "Haarlem");
        baseData.put("aggregated", "true");

        // 0
        Map<String, String> partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "116494");
        expectedDataList.add(Map.copyOf(partyData));
        // 1
        Map<String, String> candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "101086");
        expectedDataList.add(Map.copyOf(candidateData));
        // 2
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "4557");
        expectedDataList.add(Map.copyOf(candidateData));
        // 3
        partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "48721");
        expectedDataList.add(Map.copyOf(partyData));
        // 4
        candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "32733");
        expectedDataList.add(Map.copyOf(candidateData));
        // 5
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "1635");
        expectedDataList.add(Map.copyOf(candidateData));
        // 6
        Map<String, String> metadata = new HashMap<>(baseData);
        metadata.put("Cast", "714664");
        metadata.put("TotalCounted", "563110");
        metadata.put("RejectedVotes-ongeldig", "1379");
        metadata.put("RejectedVotes-blanco", "967");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "51387");
        metadata.put("UncountedVotes-geldige kiezerspassen", "1979");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "146");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "385");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "16");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "6");
        metadata.put("UncountedVotes-geen verklaring", "475");
        metadata.put("UncountedVotes-andere verklaring", "23");
        expectedDataList.add(Map.copyOf(metadata));

        baseData.put("aggregated", "false");

        Map<String, String> pollingStationData = new HashMap<>(baseData);
        pollingStationData.put("ReportingUnitIdentifier-Id", "HSB10::0358");
        pollingStationData.put("ReportingUnitIdentifier", "Aalsmeer");
        // 7
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "4898");
        expectedDataList.add(Map.copyOf(partyData));
        // 8
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "4412");
        expectedDataList.add(Map.copyOf(candidateData));
        // 9
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "168");
        expectedDataList.add(Map.copyOf(candidateData));
        // 10
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "1307");
        expectedDataList.add(Map.copyOf(partyData));
        // 11
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "870");
        expectedDataList.add(Map.copyOf(candidateData));
        // 12
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "55");
        expectedDataList.add(Map.copyOf(candidateData));
        // 13
        metadata = new HashMap<>(pollingStationData);
        metadata.put("Cast", "23647");
        metadata.put("TotalCounted", "19537");
        metadata.put("RejectedVotes-ongeldig", "35");
        metadata.put("RejectedVotes-blanco", "30");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "1790");
        metadata.put("UncountedVotes-geldige kiezerspassen", "52");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "2");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "13");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "0");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-geen verklaring", "15");
        metadata.put("UncountedVotes-andere verklaring", "0");
        expectedDataList.add(Map.copyOf(metadata));

        pollingStationData = new HashMap<>(baseData);
        pollingStationData.put("ReportingUnitIdentifier-Id", "HSB10::0362");
        pollingStationData.put("ReportingUnitIdentifier", "Amstelveen");
        // 14
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "10338");
        expectedDataList.add(Map.copyOf(partyData));
        // 15
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "9015");
        expectedDataList.add(Map.copyOf(candidateData));
        // 16
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "338");
        expectedDataList.add(Map.copyOf(candidateData));
        // 17
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "4391");
        expectedDataList.add(Map.copyOf(partyData));
        // 18
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "2905");
        expectedDataList.add(Map.copyOf(candidateData));
        // 19
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "178");
        expectedDataList.add(Map.copyOf(candidateData));
        // 20
        metadata = new HashMap<>(pollingStationData);
        metadata.put("Cast", "58073");
        metadata.put("TotalCounted", "45305");
        metadata.put("RejectedVotes-ongeldig", "149");
        metadata.put("RejectedVotes-blanco", "92");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "4191");
        metadata.put("UncountedVotes-geldige kiezerspassen", "148");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "24");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "65");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "0");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-geen verklaring", "86");
        metadata.put("UncountedVotes-andere verklaring", "0");
        expectedDataList.add(Map.copyOf(metadata));
    }

}
