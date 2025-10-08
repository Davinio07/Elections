package nl.hva.elections.xml.utils.xml.transformers;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.utils.xml.RegionTransformer;

import java.util.Map;

public class DutchRegionTransformer implements RegionTransformer {
    private final Election election;

    public DutchRegionTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerRegion(Map<String, String> electionData) {
        String id = electionData.get("Region-RegionNumber");
        String name = electionData.getOrDefault("RegionName", "");
        String category = electionData.get("Region-RegionCategory");
        String superiorCategory = electionData.get("Region-SuperiorRegionCategory");

        Region region = new Region(id, name, category, superiorCategory);

        election.addRegion(region);

        // Debug output
        System.out.println("Registered region: " + region);
    }

}
