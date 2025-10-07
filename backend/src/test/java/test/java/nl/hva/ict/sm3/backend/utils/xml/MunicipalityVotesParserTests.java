package test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MunicipalityVotesParserTests extends AbstractParserTests {

    @Test
    void testTotal2023() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        List<Map<String, String>> expectedDataList  = new LinkedList<>();
        createExpectedDataList(expectedDataList, "Telling_TK2023_gemeente_Velsen-Partial-Copy.eml.xml");
        createExpectedDataList(expectedDataList, "Telling_TK2023_gemeente_Velsen-Partial.eml.xml");

        electionProcessor.parseResults("TK2023", XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.PathUtils.getResourcePath("/%s".formatted("TK2023-Partial/Municipality")));

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
        assertEquals(22, transformer.candidateVoteCalls, "The calls to VotesTransformer.registerCandidateVote don't match!");
        assertEquals(6, transformer.metadataCalls, "The calls to VotesTransformer.registerMeta don't match!");
    }

    private void createExpectedDataList(List<Map<String, String>> expectedDataList, String fileName) throws IOException {
        Map<String, String> baseData = new HashMap<>();
        baseData.put("fileName", fileName);
        baseData.put("EML-Id", "510b");
        baseData.put("EML-SchemaVersion", "5");
        baseData.put("TransactionId", "1");
        baseData.put("AuthorityIdentifier-Id", "0453");
        baseData.put("AuthorityIdentifier", "Velsen");
        baseData.put("CreationDateTime", "2023-11-23T15:43:01.887");
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
        partyData.put("ValidVotes", "7097");
        expectedDataList.add(Map.copyOf(partyData));
        // 1
        Map<String, String> candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "6261");
        expectedDataList.add(Map.copyOf(candidateData));
        // 2
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "267");
        expectedDataList.add(Map.copyOf(candidateData));
        // 3
        partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "2672");
        expectedDataList.add(Map.copyOf(partyData));
        // 4
        candidateData = new HashMap<>(baseData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "1811");
        expectedDataList.add(Map.copyOf(candidateData));
        // 5
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "81");
        expectedDataList.add(Map.copyOf(candidateData));
        // 6
        Map<String, String> metadata = new HashMap<>(baseData);
        metadata.put("Cast", "52889");
        metadata.put("TotalCounted", "41214");
        metadata.put("RejectedVotes-ongeldig", "92");
        metadata.put("RejectedVotes-blanco", "83");
        metadata.put("UncountedVotes-geldige stempassen", "37378");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "3927");
        metadata.put("UncountedVotes-geldige kiezerspassen", "87");
        metadata.put("UncountedVotes-toegelaten kiezers", "41392");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "10");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "13");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "0");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-geen verklaring", "17");
        metadata.put("UncountedVotes-andere verklaring", "2");
        expectedDataList.add(Map.copyOf(metadata));

        baseData.put("aggregated", "false");

        Map<String, String> pollingStationData = new HashMap<>(baseData);
        pollingStationData.put("ReportingUnitIdentifier-Id", "0453::SB1");
        pollingStationData.put("ReportingUnitIdentifier", "Stembureau Gemeentehuis (postcode: 1971 EN)");
        // 7
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "263");
        expectedDataList.add(Map.copyOf(partyData));
        // 8
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "231");
        expectedDataList.add(Map.copyOf(candidateData));
        // 9
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "7");
        expectedDataList.add(Map.copyOf(candidateData));
        // 10
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "104");
        expectedDataList.add(Map.copyOf(partyData));
        // 11
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "68");
        expectedDataList.add(Map.copyOf(candidateData));
        // 12
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "0");
        expectedDataList.add(Map.copyOf(candidateData));
        // 13
        metadata = new HashMap<>(pollingStationData);
        metadata.put("Cast", "1758");
        metadata.put("TotalCounted", "2315");
        metadata.put("RejectedVotes-ongeldig", "15");
        metadata.put("RejectedVotes-blanco", "8");
        metadata.put("UncountedVotes-geldige stempassen", "2077");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "250");
        metadata.put("UncountedVotes-geldige kiezerspassen", "11");
        metadata.put("UncountedVotes-toegelaten kiezers", "2338");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "0");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "0");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "0");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-geen verklaring", "0");
        metadata.put("UncountedVotes-andere verklaring", "0");
        expectedDataList.add(Map.copyOf(metadata));

        pollingStationData = new HashMap<>(baseData);
        pollingStationData.put("ReportingUnitIdentifier-Id", "0453::SB2");
        pollingStationData.put("ReportingUnitIdentifier", "Stembureau Woon en zorgcentrum Huis ter Hagen (postcode: 1985 CV)");
        // 14
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("ValidVotes", "328");
        expectedDataList.add(Map.copyOf(partyData));
        // 15
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "282");
        expectedDataList.add(Map.copyOf(candidateData));
        // 16
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("ValidVotes", "15");
        expectedDataList.add(Map.copyOf(candidateData));
        // 17
        partyData = new HashMap<>(pollingStationData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("ValidVotes", "165");
        expectedDataList.add(Map.copyOf(partyData));
        // 18
        candidateData = new HashMap<>(pollingStationData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("ValidVotes", "114");
        expectedDataList.add(Map.copyOf(candidateData));
        // 19
        metadata = new HashMap<>(pollingStationData);
        metadata.put("Cast", "1359");
        metadata.put("TotalCounted", "1263");
        metadata.put("RejectedVotes-ongeldig", "4");
        metadata.put("RejectedVotes-blanco", "1");
        metadata.put("UncountedVotes-geldige stempassen", "1138");
        metadata.put("UncountedVotes-geldige volmachtbewijzen", "128");
        metadata.put("UncountedVotes-geldige kiezerspassen", "2");
        metadata.put("UncountedVotes-toegelaten kiezers", "1268");
        metadata.put("UncountedVotes-meer getelde stembiljetten", "0");
        metadata.put("UncountedVotes-minder getelde stembiljetten", "0");
        metadata.put("UncountedVotes-meegenomen stembiljetten", "0");
        metadata.put("UncountedVotes-te weinig uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-te veel uitgereikte stembiljetten", "0");
        metadata.put("UncountedVotes-geen verklaring", "0");
        metadata.put("UncountedVotes-andere verklaring", "0");
        expectedDataList.add(Map.copyOf(metadata));
    }

}
