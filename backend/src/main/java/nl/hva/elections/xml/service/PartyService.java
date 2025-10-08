package nl.hva.elections.xml.service;

import nl.hva.elections.xml.model.Party;
import nl.hva.elections.xml.utils.xml.transformers.PartyTransformer;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for business logic, data access, and orchestrating
 * the transformation.
 * @Service marks this class as a business service component in Spring's context,
 * allowing it to be automatically managed and injected.
 */
@Service
public class PartyService {

    private final PartyTransformer transformer;

    /**
     * Constructor Injection: Spring automatically injects the PartyTransformer,
     * which is made available because it's marked with @Component.
     */
    public PartyService(PartyTransformer transformer) {
        this.transformer = transformer;
    }

    /**
     * Inner class to mock the relevant XML structure for JAXB/parsing simulation.
     * This structure is derived from the "AffiliationIdentifier" elements in the
     * uploaded XML file (Totaaltelling_TK2023.eml.xml).
     * * Example XML structure:
     * <AffiliationIdentifier Id="1"><RegisteredName>VVD</RegisteredName></AffiliationIdentifier>
     */
    public static class AffiliationIdentifier {
        private final String id; // Corresponds to the Id attribute
        private final String registeredName; // Corresponds to the <RegisteredName> element content

        public AffiliationIdentifier(String id, String registeredName) {
            this.id = id;
            this.registeredName = registeredName;
        }

        public String getId() { return id; }
        public String getRegisteredName() { return registeredName; }
    }

    /**
     * Simulates the data access layer: reading and parsing the XML file to
     * retrieve the raw party data. In a real application, this would involve
     * using JAXB or a similar library to process the XML.
     * * @return A list of Party models extracted and transformed from the mock data.
     */
    public List<Party> getPoliticalParties() {

        // --- DATA ACCESS / PARSING MOCK START ---
        // Mock data derived from the start of the XML file:
        List<AffiliationIdentifier> rawAffiliations = List.of(
                new AffiliationIdentifier("1", "VVD"),
                new AffiliationIdentifier("2", "D66"),
                new AffiliationIdentifier("3", "PVV"),
                new AffiliationIdentifier("4", "GroenLinks"),
                new AffiliationIdentifier("5", "SP"),
                new AffiliationIdentifier("6", "CDA"),
                new AffiliationIdentifier("7", "ChristenUnie"),
                new AffiliationIdentifier("8", "PvdA"),
                new AffiliationIdentifier("9", "Partij voor de Dieren"),
                new AffiliationIdentifier("10", "50PLUS"),
                new AffiliationIdentifier("11", "SGP")
                // ... more affiliations would be included here from the full XML data
        );
        // --- DATA ACCESS / PARSING MOCK END ---

        // Use the injected Transformer to map the raw data into the clean Party model
        return transformer.toPartyList(rawAffiliations);
    }
}
