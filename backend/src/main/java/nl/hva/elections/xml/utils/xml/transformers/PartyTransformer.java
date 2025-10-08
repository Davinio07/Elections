package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Party;
import nl.hva.elections.xml.service.PartyService.AffiliationIdentifier;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Transformer responsible for converting raw data objects (simulated XML entities
 * like AffiliationIdentifier) into the application's core data Model (Party).
 * * @Component marks this class as a generic Spring component, allowing it to be
 * injected into other services.
 */
@Component
public class PartyTransformer {

    /**
     * Converts a single AffiliationIdentifier object into a Party model object.
     * @param xmlData The raw data object simulating a part of the parsed XML structure.
     * @return A clean Party model object.
     */
    public Party toParty(AffiliationIdentifier xmlData) {
        if (xmlData == null || xmlData.getId() == null || xmlData.getRegisteredName() == null) {
            // Throwing a custom exception (e.g., TransformationException) would be better practice
            throw new IllegalArgumentException("Cannot transform null or incomplete affiliation data.");
        }

        // Note: The Party model was updated to include a constructor with ID and Name
        return new Party(xmlData.getId(), xmlData.getRegisteredName());
    }

    /**
     * Converts a list of raw data objects into a list of Party models.
     * @param xmlDataList A list of raw data objects.
     * @return A list of Party model objects.
     */
    public List<Party> toPartyList(List<AffiliationIdentifier> xmlDataList) {
        if (xmlDataList == null) {
            return List.of();
        }

        return xmlDataList.stream()
                .map(this::toParty)
                .collect(Collectors.toList());
    }
}
