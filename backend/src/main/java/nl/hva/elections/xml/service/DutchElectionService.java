package nl.hva.elections.xml.service;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.utils.PathUtils;
import nl.hva.elections.xml.utils.xml.DutchElectionParser;
import nl.hva.elections.xml.utils.xml.transformers.*;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * A demo service for demonstrating how an EML-XML parser can be used inside a backend application.<br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's and FIXME's present that need fixing!</i>
 */
@Service
public class DutchElectionService {

    public Election readResults(String electionId, String folderName) {
        System.out.println("Processing files...");

        Election election = new Election(electionId);
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
            String resourcePath = PathUtils.getResourcePath("/%s".formatted(folderName));

            if (resourcePath == null) {
                throw new IOException("Resource folder not found in classpath: " + folderName);
            }

            electionParser.parseResults(electionId, resourcePath);

            // Debug output
            System.out.println("All regions: " + election.getRegions());

            return election;
        } catch (IOException | XMLStreamException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Failed to process the election results for electionId: " + electionId, e);
        }
    }

    // Only real KIESKRINGs
    public List<Region> getKieskringen(Election election) {
        return election.getRegions().stream()
                .filter(r -> "KIESKRING".equals(r.getCategory()))
                .collect(Collectors.toList());
    }


}