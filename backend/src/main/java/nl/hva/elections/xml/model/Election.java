package nl.hva.elections.xml.model;

import java.util.ArrayList;
import java.util.List;

public class Election {
    private final String id;
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Region> regions = new ArrayList<>();
    private final List<NationalResult> nationalResults = new ArrayList<>();
    private final List<PoliticalParty> politicalParties = new ArrayList<>();

    // START OF FIX: Add a List for MunicipalityResult objects
    private final List<KiesKring> municipalityResults = new ArrayList<KiesKring>();
    // END OF FIX

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

    /**
     * Returns the list of national election results.
     *
     * @return a list of {@link NationalResult} objects
     */
    public List<NationalResult> getNationalResults() {
        return nationalResults;
    }

    /**
     * Adds a new national result to the list.
     *
     * @param result the {@link NationalResult} to add
     */
    public void addNationalResult(NationalResult result) {
        nationalResults.add(result);
    }

    public List<PoliticalParty> getPoliticalParties() {
        return politicalParties;
    }

    public void addPoliticalParty(PoliticalParty party) {
        if (party != null) {
            politicalParties.add(party);
        }
    }

    public void addMunicipalityResult(KiesKring result) {
        this.municipalityResults.add(result);
    }


    public List<KiesKring> getMunicipalityResults() {
        return municipalityResults;
    }
    // END OF FIX
}