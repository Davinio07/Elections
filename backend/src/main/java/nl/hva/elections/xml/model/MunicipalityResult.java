package nl.hva.elections.xml.model;

import java.util.Objects;

/**
 * This class holds the voting results for one party in one municipality.
 * It's a simple object to store and pass data.
 */
public class MunicipalityResult {

    private String municipalityName;
    private String partyName;
    private int validVotes;

    public MunicipalityResult(String municipalityName, String partyName, int validVotes) {
        this.municipalityName = municipalityName;
        this.partyName = partyName;
        this.validVotes = validVotes;
    }

    // Getters and setters for the data fields

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public int getValidVotes() {
        return validVotes;
    }

    public void setValidVotes(int validVotes) {
        this.validVotes = validVotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MunicipalityResult that = (MunicipalityResult) o;
        return validVotes == that.validVotes &&
               Objects.equals(municipalityName, that.municipalityName) &&
               Objects.equals(partyName, that.partyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(municipalityName, partyName, validVotes);
    }
}