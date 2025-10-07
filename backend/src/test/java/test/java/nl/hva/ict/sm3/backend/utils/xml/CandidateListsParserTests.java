package test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CandidateListsParserTests extends AbstractParserTests {

    @Test
    public void testCandidates2023() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        List<Map<String, String>> expectedDataList  = new LinkedList<>();
        createExpectedDataList(expectedDataList, "Kandidatenlijsten_TK2023_Haarlem-Partial-Copy.eml.xml");
        createExpectedDataList(expectedDataList, "Kandidatenlijsten_TK2023_Haarlem-Partial.eml.xml");

        electionProcessor.parseResults("TK2023", XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.PathUtils.getResourcePath("/%s".formatted("TK2023-Partial/CandidateLists")));

        compareMaps(expectedDataList, transformer.data);
        Set<Integer> usedResults = new HashSet<>();
        for (Map<String, String> data : transformer.data) {
            usedResults.add(expectedDataList.indexOf(data));
        }
        usedResults.remove(-1);
        assertEquals(expectedDataList.size(), usedResults.size());
        assertEquals(0, transformer.regionCalls, "CandidateTransformer.registerRegion should NOT have been called!");
        assertEquals(0, transformer.partyCalls, "CandidateTransformer.registerParty should NOT have been called!");
        assertEquals(8, transformer.candidateCalls, "The calls to CandidateTransformer.registerCandidate don't match!");
        assertEquals(0, transformer.partyVoteCalls, "CandidateTransformer.registerPartyVote should NOT have been called!");
        assertEquals(0, transformer.candidateVoteCalls, "CandidateTransformer.registerCandidateVote should NOT have been called!");
        assertEquals(0, transformer.metadataCalls, "CandidateTransformer.registerMeta should NOT have been called!");
    }

    private static void createExpectedDataList(List<Map<String, String>> expectedDataList, String fileName) {
        Map<String, String> baseData = new HashMap<>();
        baseData.put("fileName", fileName);
        baseData.put("EML-Id", "230b");
        baseData.put("EML-SchemaVersion", "5");
        baseData.put("TransactionId", "1");
        baseData.put("AuthorityIdentifier-Id", "CSB");
        baseData.put("AuthorityIdentifier", "De Kiesraad");
        baseData.put("IssueDate", "2023-10-13");
        baseData.put("CreationDateTime", "2023-10-13T17:59:22.402");
        baseData.put("CanonicalizationMethod-Algorithm", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        baseData.put("ElectionIdentifier-Id", "TK2023");
        baseData.put("ElectionName", "Tweede Kamer der Staten-Generaal 2023");
        baseData.put("ElectionCategory", "TK");
        baseData.put("ElectionSubcategory", "TK");
        baseData.put("ElectionDate", "2023-11-22");
        baseData.put("NominationDate", "2023-10-09");
        baseData.put("ContestIdentifier-Id", "10");
        baseData.put("ContestName", "Haarlem");

        Map<String, String> partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "1");
        partyData.put("RegisteredName", "VVD");
        partyData.put("Type", "stel gelijkluidende lijsten");
        partyData.put("ListData-BelongsToSet", "1");
        partyData.put("ListData-PublicationLanguage", "nl");
        partyData.put("ListData-PublishGender", "true");
        // 0
        Map<String, String> candidateData = new HashMap<>(partyData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("NameLine-NameType", "Initials");
        candidateData.put("NameLine", "D.");
        candidateData.put("FirstName", "Dilan");
        candidateData.put("LastName", "Yeşilgöz");
        candidateData.put("Gender", "female");
        candidateData.put("LocalityName", "Amsterdam");
        expectedDataList.add(Map.copyOf(candidateData));
        // 1
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("NameLine", "S.T.M.");
        candidateData.put("FirstName", "Sophie");
        candidateData.put("LastName", "Hermans");
        candidateData.put("LocalityName", "Vreeland");
        expectedDataList.add(Map.copyOf(candidateData));

        partyData = new HashMap<>(baseData);
        partyData.put("AffiliationIdentifier-Id", "2");
        partyData.put("RegisteredName", "D66");
        partyData.put("Type", "stel gelijkluidende lijsten");
        partyData.put("ListData-BelongsToSet", "1");
        partyData.put("ListData-PublicationLanguage", "nl");
        partyData.put("ListData-PublishGender", "false");
        // 2
        candidateData = new HashMap<>(partyData);
        candidateData.put("CandidateIdentifier-Id", "1");
        candidateData.put("NameLine-NameType", "Initials");
        candidateData.put("NameLine", "R.A.A.");
        candidateData.put("FirstName", "Rob");
        candidateData.put("LastName", "Jetten");
        candidateData.put("LocalityName", "Ubbergen");
        expectedDataList.add(Map.copyOf(candidateData));
        // 3
        candidateData.put("CandidateIdentifier-Id", "2");
        candidateData.put("NameLine", "J.M.");
        candidateData.put("FirstName", "Jan");
        candidateData.put("LastName", "Paternotte");
        candidateData.put("LocalityName", "Leiderdorp");
        expectedDataList.add(Map.copyOf(candidateData));
    }

}
