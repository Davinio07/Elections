package nl.hva.elections.xml.model;

/**
 * Represents the total number of votes for a political party at the national level.
 */
public class NationalResult {
    private final String partyName;
    private final int validVotes;

    public NationalResult(String partyName, int validVotes) {
        this.partyName = partyName;
        this.validVotes = validVotes;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getValidVotes() {
        return validVotes;
    }

    @Override
    public String toString() {
        return "NationalResult{" +
                "partyName='" + partyName + '\'' +
                ", validVotes=" + validVotes +
                '}';
    }
}
