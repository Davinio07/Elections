package XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.xml;

import java.util.Map;

/**
 * A <code>DefinitionTransformer</code> is responsible for transforming the data into the appropriate model classes.
 */
public interface DefinitionTransformer {
    /**
     * Called whenever either a Committee opening tag or a Region closing tag is parsed.
     * @param electionData the tags and values.
     */
    void registerRegion(Map<String, String> electionData);

    /**
     * Called whenever a RegisteredParty closing tag is parsed.
     * @param electionData the tags and values.
     */
    void registerParty(Map<String, String> electionData);
}
