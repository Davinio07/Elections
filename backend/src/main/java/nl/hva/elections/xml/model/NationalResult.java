package nl.hva.elections.xml.model;

/**
 * Represents the total number of valid votes received by a political party
 * at the national level, extracted from the XML.
 */
public class NationalResult {

    /** The name of the political party. */
    private final String partyName;

    /** The total number of valid votes received by the party. */
    private final int validVotes;

    /** The number of seats the party obtained. */
    private final int seats;

    /** The percentage of total votes. */
    private final double votePercentage;

    /**
     * Creates a new national result for a political party.
     *
     * @param partyName      the name of the party
     * @param validVotes     the number of valid votes the party received
     * @param seats          the number of seats the party obtained
     * @param votePercentage the percentage of total votes
     */
    public NationalResult(String partyName, int validVotes, int seats, double votePercentage) {
        this.partyName = partyName;
        this.validVotes = validVotes;
        this.seats = seats;
        this.votePercentage = votePercentage;
    }

    /**
     * Gets the name of the political party.
     *
     * @return the party name
     */
    public String getPartyName() {
        return partyName;
    }

    /**
     * Gets the total number of valid votes received by the party.
     *
     * @return the number of valid votes
     */
    public int getValidVotes() {
        return validVotes;
    }

    /**
     * Gets the total number of seats obtained by the party.
     *
     * @return the number of seats
     */
    public int getSeats() {
        return seats;
    }

    /**
     * Gets the percentage of total votes obtained by the party.
     *
     * @return the vote percentage
     */
    public double getVotePercentage() {
        return votePercentage;
    }

    /**
     * Returns a string representation of this result.
     *
     * @return a string with the party name, vote count, seats, and percentage
     */
    @Override
    public String toString() {
        return "NationalResult{" +
                "partyName='" + partyName + '\'' +
                ", validVotes=" + validVotes +
                ", seats=" + seats +
                ", votePercentage=" + votePercentage +
                '}';
    }
}