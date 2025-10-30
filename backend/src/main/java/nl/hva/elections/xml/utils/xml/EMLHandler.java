package nl.hva.elections.xml.utils.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


import java.io.File;
import java.util.*;

/**
 * This <code>EMLHandler</code> is a SAX2 event handler that is being called by a SAX2 parser while it is
 * processing an EML-XML file. It supports three different file types.
 * <ul>
 * <li>A file containing the structure or hierarchy of the election</li>
 * <li>A file containing the candidates lists</li>
 * <li>A file containing the results from polling stations, municipalities, constituencies and the national level </li>
 * </ul>
 * Depending on the exact tag that is being processed one of the registerXxx methods from one of the transformers is
 * being called.
 * <br/>
 * The <code>EMLHandler</code> puts the values of tags and attributes in a <code>Map&ltString, String></code> called
 * <code>electionData</code>. How the values are put into this map depends on the fact if a tag has attributes or not.
 * Values are only put into the map if there actually <i>is</i> a value. For tags without attributes the tag-name is
 * used as the key. For tags with attributes, the attribute name is prefixed with the tag-name to form the key.
 * <br/>
 * Some tags need special processing. This can be done as soon as the start-tag is being parsed or when the closing-tag
 * is parsed. If and what exactly depends on the specific tag. Some tags are repeated in which case just as soon as the
 * open tag is parsed the current state of <code>electionData</code> is saved on a stack. And once the closing tag is
 * being parsed the state is popped off the stack. This ensures that each repeated tag has a clean sleight when it is
 * being processed.
 * <br/>
 * Incase an unknown tag or attribute is being processed this will be reported through standard error
 * <a href="https://en.wikipedia.org/wiki/Standard_streams">stream</a>.<br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's present that need fixing!</i>
 */
public class EMLHandler extends DefaultHandler implements TagAndAttributeNames{
    // Adding a logger. We'll use this to warn about unknown XML tags and attributes.
    private static final Logger logger = LoggerFactory.getLogger(EMLHandler.class);
    
    // The tag-name is used for the key and the value is just the value of the tag, if any
    private static final Set<String> tagsWithoutAttributes = new HashSet<>();

    // The attributes will be combined with the tag-name and serve as the key for the map containing all the data.
    private static final Map<String, Set<String>> tagsWithAttributes = new HashMap<>();
    // Temporary storage of characters like tag values and whitespace between tags.
    private final StringBuilder text = new StringBuilder();

    private RegionTransformer regionTransformer;

    // Holds the information found throughout the XML files as key-value pairs.
    private Map<String, String> electionData = new HashMap<>();
    // When processing repeating tags the current state of electionData is being preserved on this stack, and a copy is
    // being made. Once the closing tag is processed the saved 'state' of electionData is popped of the stack in order
    // to be able to start with a clean sleight when the next repeating tag is being processed.
    private final Deque<Map<String, String>> savedElectionData = new LinkedList<>();
    // The files containing the results start with the aggregated data and are followed by their respective parts.
    // When processing the results it is set to true by the constructor and once the closing tag of the
    // aggregated part is processed, it is set to false.
    private boolean aggregated;
    // When processing the structure-file (Verkeizingsdefinitie_xxx) the transformer needs to be called once the closing
    // Region tag is detected, unless a Committee is embedded in the Region. Whether the transformer needs to be called
    // is tracked by this attribute.
    private boolean registerRegion;
    // The transformer that will be called once for each committee/region and party.
    private DefinitionTransformer definitionTransformer;
    // The transformer that will be called once for each candidate in the candidates list.
    private CandidateTransformer candidateTransformer;
    // The transformer that will be called once for each party-, candidate result and the metadata.
    private VotesTransformer votesTransformer;

    // Register all the known tags and known attributes.
    static {
        // The values, if any, of these tags are put in the electionData map.
        tagsWithoutAttributes.add(AFFILIATION);
        tagsWithoutAttributes.add(AUTHORITY_ADDRESS);
        tagsWithoutAttributes.add(CANDIDATE);
        tagsWithoutAttributes.add(CANDIDATE_FULL_NAME);
        tagsWithoutAttributes.add(CANDIDATE_LIST);
        tagsWithoutAttributes.add(CAST);
        tagsWithoutAttributes.add(CONTEST);
        tagsWithoutAttributes.add(CONTEST_NAME);
        tagsWithoutAttributes.add(CONTESTS);
        tagsWithoutAttributes.add(COUNT);
        tagsWithoutAttributes.add(COUNTRY_NAME_CODE);
        tagsWithoutAttributes.add(COUNTRY);
        tagsWithoutAttributes.add(CREATION_DATE_TIME);
        tagsWithoutAttributes.add(ELECTION_TREE);
        tagsWithoutAttributes.add(ELECTED);
        tagsWithoutAttributes.add(ELECTION);
        tagsWithoutAttributes.add(ELECTION_CATEGORY);
        tagsWithoutAttributes.add(ELECTION_DATE);
        tagsWithoutAttributes.add(ELECTION_EVENT);
        tagsWithoutAttributes.add(ELECTION_NAME);
        tagsWithoutAttributes.add(ELECTION_SUBCATEGORY);
        tagsWithoutAttributes.add(EVENT_IDENTIFIER);
        tagsWithoutAttributes.add(FIRST_NAME);
        tagsWithoutAttributes.add(GENDER);
        tagsWithoutAttributes.add(ISSUE_DATE);
        tagsWithoutAttributes.add(LAST_NAME);
        tagsWithoutAttributes.add(LOCALITY);
        tagsWithoutAttributes.add(LOCALITY_NAME);
        tagsWithoutAttributes.add(MANAGING_AUTHORITY);
        tagsWithoutAttributes.add(MAX_VOTES);
        tagsWithoutAttributes.add(NAME_PREFIX);
        tagsWithoutAttributes.add(NOMINATION_DATE);
        tagsWithoutAttributes.add(NUMBER_OF_SEATS);
        tagsWithoutAttributes.add(PERSON_NAME);
        tagsWithoutAttributes.add(PREFERENCE_THRESHOLD);
        tagsWithoutAttributes.add(REGION);
        tagsWithoutAttributes.add(REGION_NAME);
        tagsWithoutAttributes.add(QUALIFYING_ADDRESS);
        tagsWithoutAttributes.add(RANKING);
        tagsWithoutAttributes.add(REGISTERED_APPELLATION);
        tagsWithoutAttributes.add(REGISTERED_NAME);
        tagsWithoutAttributes.add(REGISTERED_PARTIES);
        tagsWithoutAttributes.add(REGISTERED_PARTY);
        tagsWithoutAttributes.add(REPORTING_UNIT_VOTES);
        tagsWithoutAttributes.add(RESULT);
        tagsWithoutAttributes.add(SELECTION);
        tagsWithoutAttributes.add(SCHEMA_LOCATION);
        tagsWithoutAttributes.add(SCHEMA_VERSION);
        tagsWithoutAttributes.add(SHORT_CODE);
        tagsWithoutAttributes.add(SUPERIOR_REGION_CATEGORY);
        tagsWithoutAttributes.add(SUPERIOR_REGION_NUMBER);
        tagsWithoutAttributes.add(TOTAL_VOTES);
        tagsWithoutAttributes.add(TOTAL_COUNTED);
        tagsWithoutAttributes.add(TRANSACTION_ID);
        tagsWithoutAttributes.add(TYPE);
        tagsWithoutAttributes.add(NAME_TYPE);
        tagsWithoutAttributes.add(UNCOUNTED_VOTES);
        tagsWithoutAttributes.add(VALID_VOTES);
        tagsWithoutAttributes.add(VOTING_METHOD);
        // These are the actual keys used for these specific tags
        tagsWithAttributes.put(AFFILIATION_IDENTIFIER, Set.of(ID));
        tagsWithAttributes.put(AUTHORITY_IDENTIFIER, Set.of(ID));
        tagsWithAttributes.put(CANDIDATE_IDENTIFIER, Set.of(SHORT_CODE, ID));
        tagsWithAttributes.put(CANONICALIZATION_METHOD, Set.of(ALGORITHM));
        tagsWithAttributes.put(COMMITTEE, Set.of(ACCEPT_CENTRAL_SUBMISSIONS, COMMITTEE_CATEGORY, COMMITTEE_NAME));
        tagsWithAttributes.put(CONTEST_IDENTIFIER, Set.of(ID));
        tagsWithAttributes.put(CREATED_BY_AUTHORITY, Set.of(ID));
        tagsWithAttributes.put(ELECTION_DOMAIN, Set.of(ID));
        tagsWithAttributes.put(ELECTION_IDENTIFIER, Set.of(ID));
        tagsWithAttributes.put(EML, Set.of(ID, SCHEMA_VERSION, SCHEMA_LOCATION));
        tagsWithAttributes.put(LIST_DATA, Set.of(BELONGS_TO_COMBINATION,BELONGS_TO_SET, PUBLISH_GENDER, PUBLICATION_LANGUAGE));
        tagsWithAttributes.put(NAME_LINE, Set.of(NAME_TYPE));
        tagsWithAttributes.put(REGION, Set.of(REGION_CATEGORY, FRYSIAN_EXPORT_ALLOWED, REGION_NUMBER, REGION_NAME, SUPERIOR_REGION_NUMBER, SUPERIOR_REGION_CATEGORY));
        tagsWithAttributes.put(REJECTED_VOTES, Set.of(REASON_CODE));
        tagsWithAttributes.put(REPORTING_UNIT_IDENTIFIER, Set.of(ID));
        tagsWithAttributes.put(UNCOUNTED_VOTES, Set.of(REASON_CODE));
    }

    /**
     * Creates an EML Handler that can process the structure file.
     *
     * @param definitionTransformer the <code>DefinitionTransformer</code> that handles the transformation of the
     * provided data into the data model.
     */
    public EMLHandler(
            DefinitionTransformer definitionTransformer,
            CandidateTransformer candidateTransformer,
            RegionTransformer regionTransformer,
            VotesTransformer resultTransformer,
            VotesTransformer nationalVotesTransformer,
            VotesTransformer constituencyVotesTransformer,
            VotesTransformer municipalityVotesTransformer) {
        this.definitionTransformer = definitionTransformer;
        this.candidateTransformer = candidateTransformer;
        this.regionTransformer = regionTransformer;
        this.votesTransformer = resultTransformer;
    }


    /**
     * Creates an EML Handler that can process candidates list files. For each candidate that it finds it
     * calls the <code>registerCandidate</code> on the provided <code>candidateTransformer</code>.
     *
     * @param candidateTransformer the <code>CandidateTransformer</code> that handles the transformation of the
     * provided data into the data model.
     */
    public EMLHandler(CandidateTransformer candidateTransformer) {
        this.candidateTransformer = candidateTransformer;
    }

    /**
     * Creates an EML Handler that can process the election definition files.
     *
     * @param definitionTransformer the DefinitionTransformer that handles structural election data.
     */
    public EMLHandler(DefinitionTransformer definitionTransformer) {
        this.definitionTransformer = definitionTransformer;
    }


    /**
     * Creates an EML Handler that can process region data.
     *
     * @param regionTransformer the <code>RegionTransformer</code> that handles the transformation of region data.
     */
    public EMLHandler(RegionTransformer regionTransformer) {
        this.regionTransformer = regionTransformer;
    }

    /**
     * Creates an EML Handler that can process votes files. For each party and candidate that it finds it
     * calls the appropriate <code>registerXxxVotes</code> or the <code>registerMetadata</code>on the provided
     * <code>VotesTransformer</code>.
     *
     * @param votesTransformer the <code>VotesTransformer</code> that handles the transformation of the
     * provided data into the data model.
     */
    public EMLHandler(VotesTransformer votesTransformer) {
        aggregated = true;
        this.votesTransformer = votesTransformer;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // Pre-processing
        switch (localName) {
            case REGION:
            case COMMITTEE:
                savedElectionData.push(electionData);
                electionData = new HashMap<>(electionData);
                registerRegion = REGION.equals(localName);
        }

        // Tag processing
        if (tagsWithAttributes.containsKey(localName)) {
            // Save all the attributes and their values.
            Set<String> knownAttributes = tagsWithAttributes.get(localName);
            validateAttributes(localName, attributes, knownAttributes);
            knownAttributes.forEach(name -> {
                String attributeValue = attributes.getValue(name);
                if (attributeValue != null) {
                    electionData.put(String.format("%s-%s", localName, name).intern(), attributeValue.intern());
                }
            });
        } else if (!tagsWithoutAttributes.contains(localName)) {
            // Replaced System.err with logger.warn. It's not a critical error, but it's
            // good to know if we're encountering unexpected tags in the XML files.
            logger.warn("Ignoring unknown start tag: {}", localName);
        }

        // Post-processing
        // Depending on the exact tag different transformer methods might be called and/or the current state is saved.
        switch (localName) {
            case COMMITTEE:
                definitionTransformer.registerRegion(electionData);
                electionData = savedElectionData.pop();
                break;
            case CANDIDATE:
                // Ignore this when processing votes
                if (candidateTransformer != null) {
                    savedElectionData.push(electionData);
                    electionData = new HashMap<>(electionData);
                }
                break;
            // Fall-through is used on purpose!
            case TOTAL_VOTES:
                aggregated = true;
            case ELECTION_TREE:
            case REGISTERED_PARTY:
            case AFFILIATION: // Only appears in candidates lists
            case SELECTION: // Only appears in vote lists
            case REPORTING_UNIT_VOTES:
                // Save the current state of the information so far and create a copy that will be used
                // until it's closing tag is found.
                savedElectionData.push(electionData);
                electionData = new HashMap<>(electionData);
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        text.append(ch, start, length);
    }
    @Override
    public void endElement(String uri, String localName, String qName) {
        // Pre-processing

        // Tag processing
        if (tagsWithoutAttributes.contains(localName)) {
            // Just register the value and the tag
            String value = text.toString().trim();
            if (!value.isBlank()) {
                electionData.put(localName.intern(), value.intern());
            }
        } else if (tagsWithAttributes.containsKey(localName)) {
            // Some tags need special processing
            switch (localName) {
                case REJECTED_VOTES:
                case UNCOUNTED_VOTES:
                    // We need the value of the attribute ReasonCode as part of the actual key
                    String reasonCode = String.format("%s-%s", localName, REASON_CODE);
                    String reasonCodeKey = electionData.remove(reasonCode);
                    String count = text.toString().trim();
                    if (!count.isBlank()) {
                        electionData.put(String.format("%s-%s", localName, reasonCodeKey).intern(), count.intern());
                    }
                    break;
                default:
                    // Add the value using the tag name
                    String value = text.toString().trim();
                    if (!value.isBlank()) {
                        electionData.put(localName.intern(), value.intern());
                    }
            }
        } else {
            // Same here, using logger.warn for unknown closing tags.
            logger.warn("Ignoring unknown closing tag: {}", localName);
        }

        // Post-processing
        // Reset the text buffer. It has been saved into a String variable if needed by the time this line is executed.
        text.setLength(0);
        // Depending on the exact tag different transformer methods will be called.and/or the previous state is being
        // restored.
        switch (localName) {
            case REGION:
                // We willen alle gemeentes, kieskringen en de staat registreren.
                String category = electionData.get("Region-RegionCategory");
                if (category != null && ("KIESKRING".equals(category) || "STAAT".equals(category) || "GEMEENTE".equals(category))) {
                    if (regionTransformer != null) {
                        regionTransformer.registerRegion(electionData);
                    } else if (definitionTransformer != null) {
                        definitionTransformer.registerRegion(electionData);
                    }
                }
                electionData = savedElectionData.pop();
                break;
            case REGISTERED_PARTY:
                definitionTransformer.registerParty(electionData);
                electionData = savedElectionData.pop();
                break;
            case CANDIDATE:
                // Ignore this when processing votes
                if (candidateTransformer != null) {
                    candidateTransformer.registerCandidate(electionData);
                    electionData = savedElectionData.pop();
                }
                break;
            case SELECTION:
                // Only appears in votes lists
                if (electionData.containsKey(CANDIDATE_IDENTIFIER_ID) || electionData.containsKey(CANDIDATE_IDENTIFIER_SHORT_CODE)) {
                    votesTransformer.registerCandidateVotes(aggregated, electionData);
                } else {
                    votesTransformer.registerPartyVotes(aggregated, electionData);
                }
                electionData = savedElectionData.pop();
                break;
            case TOTAL_VOTES:
            case REPORTING_UNIT_VOTES:
                votesTransformer.registerMetadata(aggregated, electionData);
                if (TOTAL_VOTES.equals(localName)) {
                    aggregated = false;
                }
                electionData = savedElectionData.pop();
                break;
        }
    }

    private void validateAttributes(String localName, Attributes attributes, Set<String> knownAttributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (!knownAttributes.contains(attributes.getLocalName(i))) {
                // Also replaced this System.err with logger.warn.
                logger.warn("Tag {} has an unknown attribute: {}", localName, attributes.getLocalName(i));
            }
        }
    }

    public void setFileName(String fileName) {
        electionData.put("fileName", fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1));
    }
}