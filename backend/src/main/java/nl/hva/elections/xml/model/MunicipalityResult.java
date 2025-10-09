package nl.hva.elections.xml.model;

/**
 * Represents the total number of votes for a political party at the municipal level.
 */
public class MunicipalityResult {
    private final String municipalityName;
    private final String partyName;
    private final int validVotes;

    public MunicipalityResult(String municipalityName, String partyName, int validVotes) {
        this.municipalityName = municipalityName;
        this.partyName = partyName;
        this.validVotes = validVotes;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getValidVotes() {
        return validVotes;
    }

    @Override
    public String toString() {
        return "MunicipalityResult{" +
                "municipalityName='" + municipalityName + '\'' +
                ", partyName='" + partyName + '\'' +
                ", validVotes=" + validVotes +
                '}';
    }
}