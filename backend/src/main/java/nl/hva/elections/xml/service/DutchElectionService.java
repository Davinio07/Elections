package nl.hva.elections.xml.service;

import jakarta.annotation.PostConstruct; // Belangrijke import
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
 * All XML data is eagerly loaded into a cache at application startup
 * to ensure high performance for all API endpoints.
 */
@Service
public class DutchElectionService {

    private static final Logger logger = LoggerFactory.getLogger(DutchElectionService.class);

    // Lijst van alle verkiezingen die we bij de start willen laden.
    private static final List<String> ELECTION_IDS_TO_LOAD = List.of("TK2023", "TK2021");

    // De hardcoded ID voor de "standaard" loadAll-methode
    private static final String DEFAULT_ELECTION_ID = "TK2023";

    // De cache waar alle geparste data in komt
    private final Map<String, Election> electionCache = new ConcurrentHashMap<>();

    /**
     * Deze methode wordt automatisch aangeroepen nadat de service is gemaakt.
     * Het laadt alle gespecificeerde verkiezingen in de cache.
     */
    @PostConstruct
    public void initializeElectionDataCache() {
        logger.info("Starting eager parsing of XML data for {} elections...", ELECTION_IDS_TO_LOAD.size());
        for (String electionId : ELECTION_IDS_TO_LOAD) {
            try {
                // Gebruik de *private* parse-methode om de cache te vullen
                Election parsedElection = parseXmlData(electionId, electionId);
                this.electionCache.put(electionId, parsedElection);
                logger.info("Successfully parsed and cached XML data for {}", electionId);
            } catch (Exception e) {
                // Als dit mislukt, zal de applicatie niet correct werken.
                logger.error("CRITICAL STARTUP FAILURE: Failed to parse XML data for {}. Error: {}",
                        electionId, e.getMessage(), e);
            }
        }
        logger.info("Finished caching all XML election data.");
    }

    /**
     * Private helper-methode die de daadwerkelijke XML-parsing uitvoert.
     */
    private Election parseXmlData(String electionId, String folderName) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Parsing files for electionId: {} from folder: {}", electionId, folderName);

        Election election = new Election(electionId);
        // Het parsen gebeurt nu tenminste maar één keer.
        DutchElectionParser electionParser = new DutchElectionParser(
                new DutchDefinitionTransformer(election),
                new DutchCandidateTransformer(election),
                new DutchRegionTransformer(election),
                new DutchResultTransformer(election),
                new DutchNationalVotesTransformer(election),
                new DutchConstituencyVotesTransformer(election),
                new DutchKiesKringenTransformer(election)
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
        logger.info("AFTER PARSE: municipalityResults = {}", election.getKieskringResults().size());
        logger.debug("Finished parsing for {}", electionId);
        return election;
    }

    /**
     * Haalt een vooraf geparst Election-object op uit de cache.
     *
     * @param electionId De ID van de verkiezing (bv. "TK2023")
     * @return Het gecachte Election-object
     * @throws RuntimeException als er geen data voor die ID is gevonden
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
     * Laadt de "standaard" (TK2023) verkiezing.
     * Is nu een snelle cache lookup.
     */
    public Election loadAllElectionData() {
        logger.debug("Request received for 'loadAllElectionData'. Retrieving from cache...");
        return getElectionData(DEFAULT_ELECTION_ID);
    }

    /**
     * Haalt de data voor een specifieke verkiezing op uit de cache.
     * De 'folderName' parameter wordt niet meer gebruikt, maar blijft voor API-compatibiliteit.
     */
    public Election readResults(String electionId, String folderName) {
        logger.debug("Request received for 'readResults' for {}. Retrieving from cache...", electionId);
        // We negeren de folderName omdat de data al geladen is o.b.v. electionId
        return getElectionData(electionId);
    }

    // - - - Gerefactorde Service Methoden - - -
    // Ze accepteren nu een electionId en halen de data uit de cache.

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

    public List<KiesKring> getResultsForMunicipality(String electionId, String municipalityName) {
        Election election = getElectionData(electionId);

        List<KiesKring> foundResults = new ArrayList<>();
        for (KiesKring result : election.getKieskringResults()) {
            if (result.getMunicipalityName().equalsIgnoreCase(municipalityName)) {
                foundResults.add(result);
            }
        }
        foundResults.sort(Comparator.comparing(KiesKring::getValidVotes).reversed());
        return foundResults;
    }
}