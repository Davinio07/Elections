package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.utils.xml.DefinitionTransformer;

import java.util.Map;

/**
 * Transformer for handling the structure of the constituencies, municipalities and the parties.
 */
public class DutchDefinitionTransformer implements DefinitionTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the structure of the constituencies, municipalities and the parties.
     * It expects an instance of Election that can be used for storing the results.
     * @param election the election in which the votes will be stored.
     */
    public DutchDefinitionTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerRegion(Map<String, String> electionData) {
        System.out.println("Committee: " + electionData);
    }

    @Override
    public void registerParty(Map<String, String> electionData) {
        // Extract the registered appellation (party name) from the XML data
        String appellation = electionData.get("RegisteredAppellation");

        if (appellation != null && !appellation.trim().isEmpty()) {
            PoliticalParty party = new PoliticalParty(appellation);
            election.addPoliticalParty(party);
        } else {
            System.out.println("Party data (no appellation found): " + electionData);
        }
    }
}