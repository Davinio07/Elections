package nl.hva.elections.xml.service;

import jakarta.annotation.PostConstruct;
import nl.hva.elections.xml.model.*;
import nl.hva.elections.xml.utils.PathUtils;
import nl.hva.elections.xml.utils.xml.DutchElectionParser;
import nl.hva.elections.xml.utils.xml.transformers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A service for handling election data.
 * All XML data is eagerly loaded into a cache at application startup.
 */
@Service
public class DutchElectionService {

    private static final Logger logger = LoggerFactory.getLogger(DutchElectionService.class);

    // List of elections to load at startup
    private static final List<String> ELECTION_IDS_TO_LOAD = List.of("TK2023", "TK2021");

    // The default ID
    private static final String DEFAULT_ELECTION_ID = "TK2023";

    // The cache for the parsed data
    private final Map<String, Election> electionCache = new ConcurrentHashMap<>();

    /**
     * Loads the specified elections into the cache when the app starts.
     */
    @PostConstruct
    public void initializeElectionDataCache() {
        logger.info("Starting eager parsing of XML data for {} elections...", ELECTION_IDS_TO_LOAD.size());
        for (String electionId : ELECTION_IDS_TO_LOAD) {
            try {
                // Use the private parse method
                Election parsedElection = parseXmlData(electionId, electionId);
                this.electionCache.put(electionId, parsedElection);
                logger.info("Successfully parsed and cached XML data for {}", electionId);
            } catch (Exception e) {
                logger.error("CRITICAL STARTUP FAILURE: Failed to parse XML data for {}. Error: {}",
                        electionId, e.getMessage(), e);
            }
        }
        logger.info("Finished caching all XML election data.");
    }

    /**
     * Parses the XML data using the DutchElectionParser.
     */
    private Election parseXmlData(String electionId, String folderName) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Parsing files for electionId: {} from folder: {}", electionId, folderName);

        Election election = new Election(electionId);
        
        // We instantiate the parser with all the necessary transformers
        DutchElectionParser electionParser = new DutchElectionParser(
                new DutchDefinitionTransformer(election),
                new DutchCandidateTransformer(election),
                new DutchRegionTransformer(election),
                new DutchResultTransformer(election),
                new DutchNationalVotesTransformer(election),
                new DutchConstituencyVotesTransformer(election),
                new DutchMunicipalityTransformer(election)
        );

        String resourcePath = PathUtils.getResourcePath("/%s".formatted(folderName));
        if (resourcePath == null) {
            logger.error("Resource folder not found in classpath: {}", folderName);
            throw new IOException("Resource folder not found in classpath: " + folderName);
        }

        Files.walk(Paths.get(resourcePath))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".xml"))
                .forEach(p -> {
                    try {
                        electionParser.parseResults(electionId, p.toString());
                    } catch (Exception e) {
                        logger.warn("Failed to parse XML file {}: {}", p, e.getMessage());
                    }
                });
        
        logger.debug("Finished parsing for {}", electionId);
        return election;
    }

    /**
     * Retrieves a cached Election object.
     * * @param electionId The ID of the election (e.g. "TK2023")
     * @return The cached Election object
     * @throws RuntimeException if no data is found (returns empty object actually)
     */
    public Election getElectionData(String electionId) {
        Election election = electionCache.get(electionId);
        if (election == null) {
            logger.warn("No cached election data found for ID: {}. Returning empty Election.", electionId);
            return new Election(electionId);
        }
        return election;
    }

    /**
     * Loads the default election data.
     */
    public Election loadAllElectionData() {
        logger.debug("Request received for 'loadAllElectionData'. Retrieving from cache...");
        return getElectionData(DEFAULT_ELECTION_ID);
    }

    /**
     * Reads results (retrieves from cache).
     */
    public Election readResults(String electionId, String folderName) {
        logger.debug("Request received for 'readResults' for {}. Retrieving from cache...", electionId);
        return getElectionData(electionId);
    }

    public List<Region> getRegions(String electionId) {
        return getElectionData(electionId).getRegions();
    }

    public List<Region> getKieskringen(String electionId) {
        return getElectionData(electionId).getRegions().stream()
                .filter(r -> "KIESKRING".equals(r.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Region> getGemeenten(String electionId) {
        return getElectionData(electionId).getRegions().stream()
                .filter(r -> "GEMEENTE".equals(r.getCategory()))
                .collect(Collectors.toList());
    }

    public List<PoliticalParty> getPoliticalParties(String electionId) {
        return getElectionData(electionId).getPoliticalParties();
    }

    public List<String> getKiekringName(String electionId) {
        return getElectionData(electionId).getKieskringResults().stream()
                .map(KiesKring::getMunicipalityName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Gets and aggregates the results for a specific municipality.
     * I updated this to fix the issue where multiple entries per party were returned.
     */
    public List<KiesKring> getResultsForMunicipality(String electionId, String municipalityName) {
        Election election = getElectionData(electionId);

        // 1. Filter for the requested municipality
        List<KiesKring> rawResults = election.getKieskringResults().stream()
                .filter(r -> r.getMunicipalityName().equalsIgnoreCase(municipalityName))
                .toList();

        if (rawResults.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Aggregate the votes per party (Summing the integers)
        Map<String, Integer> aggregatedData = rawResults.stream()
                .collect(Collectors.groupingBy(
                        KiesKring::getPartyName,
                        Collectors.summingInt(KiesKring::getValidVotes)
                ));

        // 3. Convert back to list and sort by votes
        return aggregatedData.entrySet().stream()
                .map(entry -> new KiesKring(municipalityName, entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(KiesKring::getValidVotes).reversed())
                .collect(Collectors.toList());
    }
}