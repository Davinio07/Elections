package nl.hva.elections.dtos;

public class NationalResultDto {
    private Long id;
    private String electionId;
    private String name;
    private int votes;
    private int seats;
    private double percentage;

    public NationalResultDto(Long id, String electionId, String name, int votes,  int seats, double percentage) {
        this.id = id;
        this.electionId = electionId;
        this.name = name;
        this.votes = votes;
        this.seats = seats;
        this.percentage = percentage;
    }

    public Long getId() {
        return id;
    }

    public String getElectionId() {
        return electionId;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public int getSeats() {
        return seats;
    }

    public double getPercentage() {
        return percentage;
    }
}
