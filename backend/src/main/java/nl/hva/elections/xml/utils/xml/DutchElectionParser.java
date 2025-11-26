package nl.hva.elections.xml.utils.xml;

import nl.hva.elections.xml.utils.PathUtils;
import nl.hva.elections.xml.utils.xml.transformers.CompositeVotesTransformer;
import nl.hva.elections.xml.utils.xml.transformers.DutchRegionTransformer;
import nl.hva.elections.xml.utils.xml.transformers.DutchMunicipalityTransformer;
import org.springframework.core.io.Resource;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Processes the XML data files for the Dutch elections. It is completely model agnostic. This means that it
 * doesn't have any knowledge of the data model that is being used by the application. All the datamodel specific
 * logic must be provided by a separate classes that implements one of the Transformer interfaces.<br>
 * It can process all the files that are provided by the <a href="https://data.overheid.nl/datasets?sort=score%20desc%2C%20sys_modified%20desc&facet_authority%5B0%5D=http%3A//standaarden.overheid.nl/owms/terms/Kiesraad">Kiesraad</a>.
 * It behaves similar as the
 * <a href="https://www.baeldung.com/java-visitor-pattern">visitor pattern</a>.<br>
 * <br>
 * The data in the XML files has a more or less hierarchy structure. When a method of the transformer is called, a

 * numerical information from its {@link String} representation into its appropriate datatype.<br>
 * <br>
 * <em>It assumes that filenames have NOT been changed and that the content has not been altered!</em><br>
 * <em>Most likely you don't have to alter this class, but if you feel you need to, please feel free :-)</em><br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's present that need fixing!</i>
 */
public class DutchElectionParser {
    // Adding a logger here as well to make our parsing process observable.
    private static final Logger logger = LoggerFactory.getLogger(DutchElectionParser.class);
    
    private final DefinitionTransformer definitionTransformer;
    private final CandidateTransformer candidateTransformer;
    private final VotesTransformer resultTransformer;
    private final VotesTransformer nationalVotesTransformer;
    private final VotesTransformer constituencyVotesTransformer;
    private final VotesTransformer municipalityVotesTransformer;
    private final DutchRegionTransformer regionTransformer;


    /**
     * Creates a new instance that will use the provided transformers for transforming the data into the
     * application specific data model.
     *
     * @param definitionTransformer        the transformer that will be called while processing the structure file.
     * @param candidateTransformer         the transformer that will be called while processing the candidate lists files.
     * @param resultTransformer            the transformer that will be called while processing the result file.
     * @param nationalVotesTransformer     the transformer that will be called while processing the national votes file.
     * @param constituencyVotesTransformer the transformer that will be called while processing the constituency votes files.
     * @param municipalityVotesTransformer the transformer that will be called while processing the municipality votes files.
     */
    // TODO See the DutchElectionService for some refactoring hints for this constructor.
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
     * Traverses all the folders within the specified folder and calls the appropriate methods of the transformer.
     * While processing the files it will skip any file that has a different election-id than the one specified.
     * Currently, it only processes the files containing the 'kieslijsten' and the votes per reporting unit.
     *
     * @param electionId the identifier for the of the files that should be processed, for example <i>TK2023</i>.
     * @throws IOException in case something goes wrong while reading the file.
     * @throws XMLStreamException when a file has not the expected format.
     */
    public void parseResults(String electionId, List<Resource> allResources) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        // 1. Structure (Definitions)
        parseFiles(allResources, "Verkiezingsdefinitie_%s".formatted(electionId),
                new EMLHandler(definitionTransformer, candidateTransformer, regionTransformer, resultTransformer,
                        nationalVotesTransformer, constituencyVotesTransformer, municipalityVotesTransformer)
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
        // Filter the list to find files matching the prefix (e.g. "Totaaltelling_TK2023")
        List<Resource> matchingFiles = allResources.stream()
                .filter(r -> r.getFilename() != null && r.getFilename().startsWith(filePrefix))
                .toList();

        for (Resource resource : matchingFiles) {
            try (InputStream is = resource.getInputStream()) { // Use InputStream to read from JAR
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                SAXParser parser = factory.newSAXParser();
                emlHandler.setFileName(resource.getFilename());
                parser.parse(is, emlHandler);
            }
        }
    }
}