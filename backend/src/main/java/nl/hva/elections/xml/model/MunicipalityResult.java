package nl.hva.elections.xml.model;

import java.util.Objects;

/**
 * We renamed this class from 'KiesKring' to 'MunicipalityResult'.
 * It strictly holds the voting results for one party in one municipality.
 * This resolves the confusion where 'KiesKring' objects were holding municipality data.
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
    public int hashCode() {
        return Objects.hash(municipalityName, partyName, validVotes);
    }
}