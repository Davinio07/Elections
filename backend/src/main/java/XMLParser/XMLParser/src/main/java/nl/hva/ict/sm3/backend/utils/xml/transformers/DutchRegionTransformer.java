package XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.xml.transformers;

import XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.model.Election;
import XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.model.Region;
import XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.utils.xml.RegionTransformer;

import java.util.Map;

public class DutchRegionTransformer implements RegionTransformer {
    private final Election election;

    public DutchRegionTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerRegion(Map<String, String> electionData) {
        Region region = new Region(
                electionData.get("RegionNumber"),
                electionData.get("RegionName"),
                electionData.get("RegionCategory"),
                electionData.get("SuperiorRegionCategory")
        );

        election.addRegion(region);

        System.out.println("Registered region: " + region);
    }
}
