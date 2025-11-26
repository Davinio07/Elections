package nl.hva.elections.xml.utils.xml;

import nl.hva.elections.xml.utils.xml.transformers.CompositeVotesTransformer;
import nl.hva.elections.xml.utils.xml.transformers.DutchRegionTransformer;
import nl.hva.elections.xml.utils.xml.transformers.DutchMunicipalityTransformer;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Processes the XML data files for the Dutch elections.
 * UPDATED for JAR Deployment: Now uses Spring Resources instead of File Paths.
 */
public class DutchElectionParser {

    private static final Logger logger = LoggerFactory.getLogger(DutchElectionParser.class);

    private final DefinitionTransformer definitionTransformer;
    private final CandidateTransformer candidateTransformer;
    private final VotesTransformer resultTransformer;
    private final VotesTransformer nationalVotesTransformer;
    private final VotesTransformer constituencyVotesTransformer;
    private final VotesTransformer municipalityVotesTransformer;
    private final DutchRegionTransformer regionTransformer;

    public DutchElectionParser(DefinitionTransformer definitionTransformer,
                               CandidateTransformer candidateTransformer,
                               DutchRegionTransformer dutchRegionTransformer,
                               VotesTransformer resultTransformer,
                               VotesTransformer nationalVotesTransformer,
                               VotesTransformer constituencyVotesTransformer,
                               VotesTransformer municipalityVotesTransformer) {
        this.definitionTransformer = definitionTransformer;
        this.candidateTransformer = candidateTransformer;
        this.resultTransformer = resultTransformer;
        this.nationalVotesTransformer = nationalVotesTransformer;
        this.constituencyVotesTransformer = constituencyVotesTransformer;
        this.municipalityVotesTransformer = municipalityVotesTransformer;
        this.regionTransformer = dutchRegionTransformer;
    }

    /**
     * Traverses the provided list of Resources and calls the appropriate methods of the transformer.
     * * @param electionId the identifier for the election (e.g. TK2023).
     * @param allResources The list of all XML files found in the classpath.
     */
    public void parseResults(String electionId, List<Resource> allResources) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Parsing {} resources for election {}", allResources.size(), electionId);

        // 1. Structure (Definitions)
        parseFiles(allResources, "Verkiezingsdefinitie_%s".formatted(electionId),
                new EMLHandler(
                        definitionTransformer,
                        candidateTransformer,
                        regionTransformer,
                        resultTransformer,
                        nationalVotesTransformer,
                        constituencyVotesTransformer,
                        municipalityVotesTransformer
                )
        );

        // 2. Candidates
        parseFiles(allResources, "Kandidatenlijsten_%s".formatted(electionId), new EMLHandler(candidateTransformer));

        // 3. Results (Seats)
        parseFiles(allResources, "Resultaat_%s".formatted(electionId), new EMLHandler(resultTransformer));

        // 4. National Total
        VotesTransformer nationalComposite = new CompositeVotesTransformer(nationalVotesTransformer, municipalityVotesTransformer);
        parseFiles(allResources, "Totaaltelling_%s".formatted(electionId), new EMLHandler(nationalComposite));

        // 5. Constituency Files
        VotesTransformer kieskringComposite = new CompositeVotesTransformer(constituencyVotesTransformer, municipalityVotesTransformer);
        parseFiles(allResources, "Telling_%s_kieskring".formatted(electionId), new EMLHandler(kieskringComposite));
    }

    private void parseFiles(List<Resource> allResources, String filePrefix, EMLHandler emlHandler) throws IOException, ParserConfigurationException, SAXException {
        // Filter the list to find files matching the prefix
        List<Resource> matchingFiles = allResources.stream()
                .filter(r -> r.getFilename() != null && r.getFilename().startsWith(filePrefix))
                .toList();

        for (Resource resource : matchingFiles) {
            // Use InputStream to read from inside the JAR
            try (InputStream is = resource.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(is, 64 * 1024)) {

                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                SAXParser parser = factory.newSAXParser();
                emlHandler.setFileName(resource.getFilename());
                parser.parse(bis, emlHandler);
            } catch (Exception e) {
                logger.error("Failed to parse file: {}", resource.getFilename(), e);
            }
        }
    }
}