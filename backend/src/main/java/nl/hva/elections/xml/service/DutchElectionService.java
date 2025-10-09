package nl.hva.elections.xml.service;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.utils.PathUtils;
import nl.hva.elections.xml.utils.xml.DutchElectionParser;
import nl.hva.elections.xml.utils.xml.transformers.*;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A demo service for demonstrating how an EML-XML parser can be used inside a backend application.<br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's and FIXME's present that need fixing!</i>
 */
@Service
public class DutchElectionService {

    private static final String ELECTION_ID = "TK2023";
    private static final String ELECTION_DATA_FOLDER = "/TK2023_HvA_UvA";

    // ===================================================================================
    // NIEUWE METHODE VOOR DE FRONTEND (MINIMALE TOEVOEGING)
    // ===================================================================================

    /**
     * Loads and parses all election data from the resources folder.
     * This method is used by the frontend to get all data at once.
     */
    public Election loadAllElectionData() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        Election election = new Election(ELECTION_ID);

        // Instantiate all the required transformers
        DutchDefinitionTransformer definitionTransformer = new DutchDefinitionTransformer(election);
        DutchCandidateTransformer candidateTransformer = new DutchCandidateTransformer(election);
        DutchRegionTransformer regionTransformer = new DutchRegionTransformer(election);
        DutchNationalVotesTransformer nationalVotesTransformer = new DutchNationalVotesTransformer(election);
        DutchConstituencyVotesTransformer constituencyVotesTransformer = new DutchConstituencyVotesTransformer(election);
        DutchMunicipalityVotesTransformer municipalityVotesTransformer = new DutchMunicipalityVotesTransformer(election);
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
            parser.parseResults(ELECTION_ID, resourcePath);
        } else {
            throw new IOException("Resource folder not found in classpath: " + ELECTION_DATA_FOLDER);
        }


        return election;
    }


    // ===================================================================================
    // ALLE ORIGINELE METHODES ZIJN HIERONDER BEHOUDEN
    // ===================================================================================

    public Election readResults(String electionId, String folderName) {
        System.out.println("Processing files...");

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
                new DutchMunicipalityVotesTransformer(election)
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

            // Do what ever you like to do
            System.out.println("Dutch Election results: " + election);
            // Now is also the time to send the election information to a database for example.
            System.out.println("National results: " + election.getNationalResults());
            System.out.println("Regions: " + election.getRegions());

            return election;
        } catch (IOException | XMLStreamException | ParserConfigurationException | SAXException e) {
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

    public List<NationalResult> getNationalResults(String electionId) {
        Election election = readResults(electionId, electionId);
        return election.getNationalResults();
    }
}