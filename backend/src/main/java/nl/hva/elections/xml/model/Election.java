package nl.hva.elections.xml.model;

import java.util.ArrayList;
import java.util.List;

public class Election {
    private final String id;
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Region> regions = new ArrayList<>();
    private final List<NationalResult> nationalResults = new ArrayList<>();


    public Election(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    public List<NationalResult> getNationalResults() {
        return nationalResults;
    }

    public void addNationalResult(NationalResult result) { nationalResults.add(result);}
}
