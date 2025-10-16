package nl.hva.elections.xml.utils.xml;

import nl.hva.elections.xml.utils.PathUtils;
import nl.hva.elections.xml.utils.xml.transformers.CompositeVotesTransformer;
import nl.hva.elections.xml.utils.xml.transformers.DutchRegionTransformer;
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
import java.util.Map;

/**
 * Processes the XML data files for the Dutch elections. It is completely model agnostic. This means that it
 * doesn't have any knowledge of the data model that is being used by the application. All the datamodel specific
 * logic must be provided by a separate classes that implements one of the Transformer interfaces.<br>
 * It can process all the files that are provided by the <a href="https://data.overheid.nl/datasets?sort=score%20desc%2C%20sys_modified%20desc&facet_authority%5B0%5D=http%3A//standaarden.overheid.nl/owms/terms/Kiesraad">Kiesraad</a>.
 * It behaves similar as the
 * <a href="https://www.baeldung.com/java-visitor-pattern">visitor pattern</a>.<br>
 * <br>
 * The data in the XML files has a more or less hierarchy structure. When a method of the transformer is called, a
 * {@link Map} containing all the information on that level, including the information at the higher levels,
 * is provided. The {@link Map} is specified as: Map&lt;String, String>. It is up to the transformer to convert any
 * numerical information from its {@link String} representation into its appropriate datatype.<br>
 * <br>
 * <em>It assumes that filenames have NOT been changed and that the content has not been altered!</em><br>
 * <em>Most likely you don't have to alter this class, but if you feel you need to, please feel free :-)</em><br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's present that need fixing!</i>
 */
public class DutchElectionParser {
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
                               DutchRegionTransformer dutchRegionTransformer, VotesTransformer resultTransformer,
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
     * @param folderName The name of the folder that contains the files containing the election data.
     * @throws IOException in case something goes wrong while reading the file.
     * @throws XMLStreamException when a file has not the expected format.
     */
    public void parseResults(String electionId, String folderName) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        System.out.printf("Loading election data from %s\n", folderName);

        // The first call to parseFiles needs to use all seven transformers
        // to correctly load all the initial data, including regions and candidates.
        parseFiles(folderName, "Verkiezingsdefinitie_%s".formatted(electionId),
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

        // These lines handle the remaining files and use the correct EMLHandler constructors.
        parseFiles(folderName, "Kandidatenlijsten_%s".formatted(electionId), new EMLHandler(candidateTransformer));
        parseFiles(folderName, "Resultaat_%s".formatted(electionId), new EMLHandler(resultTransformer));

        // Create a composite transformer that sends Totaaltelling data to BOTH national and municipality transformers.
        VotesTransformer compositeTransformer = new CompositeVotesTransformer(nationalVotesTransformer, municipalityVotesTransformer);
        parseFiles(folderName, "Totaaltelling_%s".formatted(electionId), new EMLHandler(compositeTransformer));

        // Parse the kieskring files.
        parseFiles(folderName, "Telling_%s_kieskring".formatted(electionId), new EMLHandler(constituencyVotesTransformer));
    }

    private void parseFiles(String folderName, String fileFilter, EMLHandler emlHandler) throws IOException, ParserConfigurationException, SAXException {
        List<Path> files = PathUtils.findFilesToScan(folderName, fileFilter);
        files.sort(Comparator.comparing(Path::getFileName));
        for (Path electionFile : files) {
            // TODO replace with proper usage of a logging framework
            System.out.printf("Processing: %s\n", electionFile);
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(electionFile.toString()), 64 * 1024)) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                SAXParser parser = factory.newSAXParser();
                emlHandler.setFileName(electionFile.toString());
                parser.parse(bis, emlHandler);
            }
            // TODO replace with proper usage of a logging framework
            System.out.printf("Processed: %s\n", electionFile);
        }
    }
}
