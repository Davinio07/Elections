package nl.hva.elections.xml.service;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;
/**
 * A demo service for demonstrating how an EML-XML parser can be used inside a backend application.<br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's and FIXME's present that need fixing!</i>
 */
@Service
public class DutchElectionService {

    // Added a logger here, just like in the controller.
    private static final Logger logger = LoggerFactory.getLogger(DutchElectionService.class);

    private static final String ELECTION_ID = "TK2023";
    private static final String ELECTION_DATA_FOLDER = "/TK2023_HvA_UvA";


    /**
     * Loads and parses all election data from the resources folder.
     * This method is used by the frontend to get all data at once.
     */
    public Election loadAllElectionData() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Starting to load all election data for election ID: {}", ELECTION_ID);
        Election election = new Election(ELECTION_ID);

        // Instantiate all the required transformers
        DutchDefinitionTransformer definitionTransformer = new DutchDefinitionTransformer(election);
        DutchCandidateTransformer candidateTransformer = new DutchCandidateTransformer(election);
        DutchRegionTransformer regionTransformer = new DutchRegionTransformer(election);
        DutchNationalVotesTransformer nationalVotesTransformer = new DutchNationalVotesTransformer(election);
        DutchConstituencyVotesTransformer constituencyVotesTransformer = new DutchConstituencyVotesTransformer(election);
        DutchKiesKringenTransformer municipalityVotesTransformer = new DutchKiesKringenTransformer(election);
        DutchResultTransformer resultTransformer = new DutchResultTransformer(election);

        // Create the parser with all the transformers in the correct order
        DutchElectionParser parser = new DutchElectionParser(
                definitionTransformer,
                candidateTransformer,
                regionTransformer,
                resultTransformer,
                nationalVotesTransformer,
                constituencyVotesTransformer,
                municipalityVotesTransformer
        );

        // Use PathUtils to find the root folder of the election data
        String resourcePath = PathUtils.getResourcePath(ELECTION_DATA_FOLDER);

        // Let the parser handle finding and parsing all the files
        if (resourcePath != null) {
            logger.info("Found election data folder at: {}", resourcePath);
            parser.parseResults(ELECTION_ID, resourcePath);
        } else {
            // We need to throw an exception if the data isn't there.
            throw new IOException("Resource folder not found in classpath: " + ELECTION_DATA_FOLDER);
        }

        logger.info("Finished loading all election data for election ID: {}", ELECTION_ID);
        return election;
    }



    public Election readResults(String electionId, String folderName) {
        logger.info("Processing files for electionId: {} from folder: {}", electionId, folderName);

        Election election = new Election(electionId);
        // TODO This lengthy construction of the parser should be replaced with a fitting design pattern!
        //  And refactoring the constructor while your at it is also a good idea.
        DutchElectionParser electionParser = new DutchElectionParser(
                new DutchDefinitionTransformer(election),
                new DutchCandidateTransformer(election),
                new DutchRegionTransformer(election),
                new DutchResultTransformer(election),
                new DutchNationalVotesTransformer(election),
                new DutchConstituencyVotesTransformer(election),
                new DutchKiesKringenTransformer(election)
        );

        try {
            // Assuming the election data is somewhere on the class-path it should be found.
            // Please note that you can also specify an absolute path to the folder!
            String resourcePath = PathUtils.getResourcePath("/%s".formatted(folderName));

            // Check if the resource path is found. If not, throw an exception.
            if (resourcePath == null) {
                throw new IOException("Resource folder not found in classpath: " + folderName);
            }

            electionParser.parseResults(electionId, resourcePath);

            // Let's log some details after parsing. I'm using DEBUG level because this is pretty verbose
            // and not something we want to see in production logs unless we are debugging.
            logger.debug("Dutch Election results: {}", election);
            // Now is also the time to send the election information to a database for example.
            logger.debug("National results count: {}", election.getNationalResults().size());
            logger.debug("Regions count: {}", election.getRegions().size());

            return election;
        } catch (IOException | XMLStreamException | ParserConfigurationException | SAXException e) {
            // Good practice: log the exception here before we re-throw it.
            // This way, we know exactly where the error started.
            logger.error("Failed to process the election results for electionId: {}", electionId, e);
            // Throw a runtime exception with a clear message to be handled by the controller advice or error handler.
            throw new RuntimeException("Failed to process the election results for electionId: " + electionId, e);
        }
    }

    // Only real KIESKRINGs
    public List<Region> getKieskringen(Election election) {
        return election.getRegions().stream()
                .filter(r -> "KIESKRING".equals(r.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all municipalities from the election data.
     * @param election The fully loaded election object.
     * @return A list of Region objects filtered to only include municipalities.
     */
    public List<Region> getGemeenten(Election election) {
        return election.getRegions().stream()
                .filter(r -> "GEMEENTE".equals(r.getCategory()))
                .collect(Collectors.toList());
    }

    public List<NationalResult> getNationalResults(String electionId) {
        Election election = readResults(electionId, electionId);
        return election.getNationalResults();
    }

    /**
     * Gets a unique and sorted list of all municipality names.
     * This is used to populate the search dropdown in the frontend.
     * @param election The fully loaded election object.
     * @return A list of strings, where each string is a unique municipality name.
     */
    public List<String> getMunicipalityNames(Election election) {
        return election.getMunicipalityResults().stream()
                .map(KiesKring::getMunicipalityName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * This method finds the election results for a specific municipality.
     * @param election The object that holds all the election data.
     * @param municipalityName The name of the municipality we are looking for.
     * @return A list of results for that municipality, sorted by votes.
     */
    public List<KiesKring> getResultsForMunicipality(Election election, String municipalityName) {
        // Create an empty list to hold our results
        List<KiesKring> foundResults = new ArrayList<>();

        // Loop through all municipality results in the election data
        for (KiesKring result : election.getMunicipalityResults()) {
            // Check if the municipality name is the one we want (ignoring case)
            if (result.getMunicipalityName().equalsIgnoreCase(municipalityName)) {
                // If it is, add it to our new list
                foundResults.add(result);
            }
        }

        // Sort the list of results based on the number of valid votes
        foundResults.sort(Comparator.comparing(KiesKring::getValidVotes).reversed());

        // Return the sorted list
        return foundResults;
    }

    /**
     * This function calculates seat distribution in an election using the D'Hondt method.
     *
     * @param results    A list of {@code NationalResult} objects, where each object contains
     * the party's name and its total number of valid votes.
     * @param totalSeats The total number of seats to be allocated.
     * @return A {@code Map<String, Integer>} where the keys are the party names and the
     * values are the number of seats allocated to each party.
     */
    public Map<String, Integer> calculateSeats(List<NationalResult> results, int totalSeats) {
        Map<String, Integer> seats = new HashMap<>();
        Map<String, Integer> voteCounts = results.stream()
                .collect(Collectors.toMap(NationalResult::getPartyName, NationalResult::getValidVotes));

        for (String party : voteCounts.keySet()) {
            seats.put(party, 0); // initialize seats
        }

        for (int i = 0; i < totalSeats; i++) {
            String maxParty = null;
            double maxValue = -1;
            for (String party : voteCounts.keySet()) {
                int votes = voteCounts.get(party);
                int allocatedSeats = seats.get(party);
                double value = votes / (allocatedSeats + 1.0); // D’Hondt formula
                if (value > maxValue) {
                    maxValue = value;
                    maxParty = party;
                }
            }
            if (maxParty != null) {
                seats.put(maxParty, seats.get(maxParty) + 1);
            }
        }

        return seats;
    }
}