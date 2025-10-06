package XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Election {
    private final String id;
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Region> regions = new ArrayList<>();


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

    @Override
    public String toString() {
        return "Election{" +
                "id='" + id + '\'' +
                ", candidates=" + candidates +
                '}';
    }
}
