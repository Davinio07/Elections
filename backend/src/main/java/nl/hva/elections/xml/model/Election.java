package nl.hva.elections.xml.model;

import java.util.ArrayList;
import java.util.List;

public class Election {
    private final String id;
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Region> regions = new ArrayList<>();
    private final List<Party> nationalResults = new ArrayList<>();
    private final List<PoliticalParty> politicalParties = new ArrayList<>();

    // We renamed this list to strictly reflect that it holds municipality results.
    private final List<MunicipalityResult> municipalityResults = new ArrayList<>();

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
     * @return a list of {@link Party} objects
     */
    public List<Party> getNationalResults() {
        return nationalResults;
    }

    /**
     * Adds a new national result to the list.
     *
     * @param result the {@link Party} to add
     */
    public void addNationalResult(Party result) {
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

    // We updated the method signature to accept our new MunicipalityResult class
    public void addMunicipalityResult(MunicipalityResult result) {
        this.municipalityResults.add(result);
    }

    public List<MunicipalityResult> getMunicipalityResults() {
        return municipalityResults;
    }
}