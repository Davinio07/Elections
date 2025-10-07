package test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NationalVotesParserTests extends AbstractParserTests {

    @Test
    void testTotal2023() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        List<Map<String, String>> expectedDataList  = new LinkedList<>();
        createExpectedDataList(expectedDataList, "Totaaltelling_TK2023-Partial.eml.xml");

        electionProcessor.parseResults("TK2023", XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.PathUtils.getResourcePath("/%s".formatted("TK2023-Partial/National")));

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
        assertEquals(6, transformer.partyVoteCalls, "The calls to VotesTransformer.registerPartyVote don't match!");
        assertEquals(12, transformer.candidateVoteCalls, "The calls to VotesTransformer.registerCandidateVote don't match!");
        assertEquals(3, transformer.metadataCalls, "The calls to VotesTransformer.registerMeta don't match!");
    }

    private static void createExpectedDataList(List<Map<String, String>> expectedDataList, String fileName) {
        Map<String, String> baseData = new HashMap<>();
        baseData.put("fileName", fileName);
        baseData.put("EML-Id", "510d");
        baseData.put("EML-SchemaVersion", "5");
        baseData.put("TransactionId", "1");
        baseData.put("AuthorityIdentifier-Id", "CSB");
        baseData.put("AuthorityIdentifier", "De Kiesraad");
        baseData.put("CreationDateTime", "2023-12-03T14:20:55.204");
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
        partyData.put("ValidVotes", "1589519");
        expectedDataList.add(Map.copyOf(partyData));
        // 1
        Map<String, String> candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-ShortCode", "YeşilgözD");
        candidateData.put("ValidVotes", "1356883");
        expectedDataList.add(Map.copyOf(candidateData));
        // 2
        candidateData.put("CandidateIdentifier-ShortCode", "HermansSTM");
        candidateData.put("ValidVotes", "62320");
        expectedDataList.add(Map.copyOf(candidateData));
        // 3
        partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "656292");
        expectedDataList.add(Map.copyOf(partyData));
        // 4
        candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-ShortCode", "JettenRAA");
        candidateData.put("ValidVotes", "437371");
        expectedDataList.add(Map.copyOf(candidateData));
        // 5
        candidateData.put("CandidateIdentifier-ShortCode", "PaternotteJM");
        candidateData.put("ValidVotes", "19645");
        expectedDataList.add(Map.copyOf(candidateData));
        // 6
        Map<String, String> metadata = new HashMap<>(baseData);
        metadata.put("Cast", "13473750");
        metadata.put("TotalCounted", "10432726");
        metadata.put("RejectedVotes-ongeldig", "22822");
        metadata.put("RejectedVotes-blanco", "19655");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "996512");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "3038");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "5647");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "636");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "167");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "269");
        metadata.put("UncountedVotes-geen verklaring", "4971");
        metadata.put("UncountedVotes-andere verklaring", "2229");
        metadata.put("UncountedVotes-kwijtgeraakte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel briefstembiljetten", "27");
        metadata.put("UncountedVotes-geen briefstembiljetten", "122");
        expectedDataList.add(Map.copyOf(metadata));

        baseData.put("aggregated", "false");

        Map<String, String> constituencyData = new HashMap<>(baseData);
        constituencyData.put("ReportingUnitIdentifier-Id", "HSB1");
        constituencyData.put("ReportingUnitIdentifier", "Kieskring Groningen");
        // 7
        partyData = new HashMap<>(constituencyData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "36092");
        expectedDataList.add(Map.copyOf(partyData));
        // 8
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("CandidateIdentifier-ShortCode", "YeşilgözD");
        candidateData.put("ValidVotes", "28698");
        expectedDataList.add(Map.copyOf(candidateData));
        // 9
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("CandidateIdentifier-ShortCode", "HermansSTM");
        candidateData.put("ValidVotes", "1169");
        expectedDataList.add(Map.copyOf(candidateData));
        // 10
        partyData = new HashMap<>(constituencyData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "20899");
        expectedDataList.add(Map.copyOf(partyData));
        // 11
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("CandidateIdentifier-ShortCode", "JettenRAA");
        candidateData.put("ValidVotes", "11083");
        expectedDataList.add(Map.copyOf(candidateData));
        // 12
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("CandidateIdentifier-ShortCode", "PaternotteJM");
        candidateData.put("ValidVotes", "430");
        expectedDataList.add(Map.copyOf(candidateData));
        // 13
        metadata = new HashMap<>(constituencyData);
        metadata.put("Cast", "467390");
        metadata.put("TotalCounted", "365627");
        metadata.put("RejectedVotes-ongeldig", "682");
        metadata.put("RejectedVotes-blanco", "779");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "29726");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "80");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "131");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "36");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "60");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "21");
        metadata.put("UncountedVotes-geen verklaring", "134");
        metadata.put("UncountedVotes-andere verklaring", "8");
        expectedDataList.add(Map.copyOf(metadata));

        constituencyData = new HashMap<>(baseData);
        constituencyData.put("ReportingUnitIdentifier-Id", "HSB2");
        constituencyData.put("ReportingUnitIdentifier", "Kieskring Leeuwarden");
        // 14
        partyData = new HashMap<>(constituencyData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "47098");
        expectedDataList.add(Map.copyOf(partyData));
        // 15
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("CandidateIdentifier-ShortCode", "YeşilgözD");
        candidateData.put("ValidVotes", "39603");
        expectedDataList.add(Map.copyOf(candidateData));
        // 16
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("CandidateIdentifier-ShortCode", "HermansSTM");
        candidateData.put("ValidVotes", "1465");
        expectedDataList.add(Map.copyOf(candidateData));
        // 17
        partyData = new HashMap<>(constituencyData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "18249");
        expectedDataList.add(Map.copyOf(partyData));
        // 18
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("CandidateIdentifier-ShortCode", "JettenRAA");
        candidateData.put("ValidVotes", "11945");
        expectedDataList.add(Map.copyOf(candidateData));
        // 19
        candidateData = new HashMap<>(constituencyData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("CandidateIdentifier-ShortCode", "PaternotteJM");
        candidateData.put("ValidVotes", "459");
        expectedDataList.add(Map.copyOf(candidateData));
        // 20
        metadata = new HashMap<>(constituencyData);
        metadata.put("Cast", "520759");
        metadata.put("TotalCounted", "419599");
        metadata.put("RejectedVotes-ongeldig", "673");
        metadata.put("RejectedVotes-blanco", "849");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "35377");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "98");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "122");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "29");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "15");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "17");
        metadata.put("UncountedVotes-geen verklaring", "98");
        metadata.put("UncountedVotes-andere verklaring", "42");
        expectedDataList.add(Map.copyOf(metadata));
    }

}
