package test.java.nl.hva.ict.sm3.backend.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructureParserTests extends AbstractParserTests {

    @Test
	void testStructure2023() throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
        List<Map<String, String>> expectedDataList  = new LinkedList<>();
        createExpectedDataList(expectedDataList, "Verkiezingsdefinitie_TK2023-Partial.eml.xml");

        electionProcessor.parseResults("TK2023", XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.PathUtils.getResourcePath("/%s".formatted("TK2023-Partial/Structure")));

        compareMaps(expectedDataList, transformer.data);
        Set<Integer> usedResults = new HashSet<>();
        for (Map<String, String> data : transformer.data) {
            usedResults.add(expectedDataList.indexOf(data));
        }
        usedResults.remove(-1);
        assertEquals(expectedDataList.size(), usedResults.size());
        assertEquals(9, transformer.regionCalls, "The calls to StructureTransformer.registerRegion don't match!");
        assertEquals(2, transformer.partyCalls, "The calls to StructureTransformer.registerParty don't match!");
        assertEquals(0, transformer.candidateCalls, "StructureTransformer.registerCandidate should NOT have been called!");
        assertEquals(0, transformer.partyVoteCalls, "StructureTransformer.registerPartyVote should NOT have been called!");
        assertEquals(0, transformer.candidateVoteCalls, "StructureTransformer.registerCandidateVote should NOT have been called!");
        assertEquals(0, transformer.metadataCalls, "StructureTransformer.registerMeta should NOT have been called!");
    }

    private static void createExpectedDataList(List<Map<String, String>> expectedDataList, String fileName) {
        Map<String, String> baseData = new HashMap<>();
        baseData.put("fileName", fileName);
        baseData.put("EML-Id", "110a");
        baseData.put("EML-SchemaVersion", "5");
        baseData.put("TransactionId", "1");
        baseData.put("IssueDate", "2023-10-13");
        baseData.put("CreationDateTime", "2023-10-13T17:59:22.402");
        baseData.put("ElectionIdentifier-Id", "TK2023");
        baseData.put("ElectionName", "Tweede Kamer der Staten-Generaal 2023");
        baseData.put("ElectionCategory", "TK");
        baseData.put("ElectionSubcategory", "TK");
        baseData.put("ElectionDate", "2023-11-22");
        baseData.put("NominationDate", "2023-10-09");
        baseData.put("ContestIdentifier-Id", "alle");
        baseData.put("VotingMethod", "SPV");
        baseData.put("NumberOfSeats", "150");
        baseData.put("PreferenceThreshold", "25");

        // 0
        Map<String, String> committeeData = new HashMap<>(baseData);
        committeeData.put("Region-RegionCategory", "STAAT");
        committeeData.put("RegionName", "Nederland");
        expectedDataList.add(Map.copyOf(committeeData));
        // 1
        committeeData = new HashMap<>(baseData);
        committeeData.put("Region-RegionNumber", "1");
        committeeData.put("Region-RegionCategory", "KIESKRING");
        committeeData.put("Region-SuperiorRegionCategory", "STAAT");
        committeeData.put("RegionName", "Groningen");
        committeeData.put("Committee-CommitteeCategory", "HSB");
        expectedDataList.add(Map.copyOf(committeeData));
        // 2
        committeeData.put("Region-RegionNumber", "2");
        committeeData.put("RegionName", "Leeuwarden");
        expectedDataList.add(Map.copyOf(committeeData));
        // 3
        committeeData.put("Region-RegionNumber", "12");
        committeeData.put("RegionName", "'s-Gravenhage");
        committeeData.put("Committee-CommitteeCategory", "CSB");
        committeeData.put("Committee-CommitteeName", "De Kiesraad");
        expectedDataList.add(Map.copyOf(committeeData));
        // 4
        committeeData.remove("Committee-CommitteeName");
        committeeData.put("Committee-CommitteeCategory", "HSB");
        committeeData.put("Committee-AcceptCentralSubmissions", "true");
        expectedDataList.add(Map.copyOf(committeeData));
        // 5
        committeeData = new HashMap<>(baseData);
        committeeData.put("Region-RegionNumber", "14");
        committeeData.put("Region-RegionCategory", "GEMEENTE");
        committeeData.put("Region-SuperiorRegionNumber", "1");
        committeeData.put("Region-SuperiorRegionCategory", "KIESKRING");
        committeeData.put("RegionName", "Groningen");
        expectedDataList.add(Map.copyOf(committeeData));
        // 6
        committeeData.put("Region-RegionNumber", "37");
        committeeData.put("RegionName", "Stadskanaal");
        expectedDataList.add(Map.copyOf(committeeData));
        // 7
        committeeData.put("Region-RegionNumber", "47");
        committeeData.put("RegionName", "Veendam");
        expectedDataList.add(Map.copyOf(committeeData));
        // 8
        committeeData.put("Region-RegionNumber", "765");
        committeeData.put("RegionName", "Pekela");
        expectedDataList.add(Map.copyOf(committeeData));
        // 9
        Map<String, String> affiliationData = new HashMap<>(baseData);
        affiliationData.put("RegisteredAppellation", "VVD");
        expectedDataList.add(Map.copyOf(affiliationData));
        // 10
        affiliationData.put("RegisteredAppellation", "D66");
        expectedDataList.add(Map.copyOf(affiliationData));
    }
}
