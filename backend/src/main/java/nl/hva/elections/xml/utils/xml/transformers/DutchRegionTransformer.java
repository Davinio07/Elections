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

        // --- FIX START: Prioritize the direct XML Tag over the Attribute ---
        // 1. Try to get the direct tag value (e.g. <SuperiorRegionNumber>3</...>)
        String superiorRegionNumber = electionData.get("SuperiorRegionNumber");

        // 2. If that is null, ONLY THEN check the attribute (Region-SuperiorRegionNumber)
        // This prevents us from accidentally grabbing the Parent's attribute value.
        if (superiorRegionNumber == null) {
            superiorRegionNumber = electionData.get("Region-SuperiorRegionNumber");
        }
        // --- FIX END ---

        Region region = new Region(id, name, category, superiorCategory, superiorRegionNumber);
        election.addRegion(region);
    }
}