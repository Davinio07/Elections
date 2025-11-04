package nl.hva.elections.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String electionId; // e.g., "TK2021", "TK2023"
    private String name;

    @JsonProperty("totalVotes")
    private int votes;

    @JsonProperty("nationalSeats")
    private int seats;

    @JsonProperty("votePercentage")
    private double percentage;

    /**
     * Required default constructor for JPA/Hibernate.
     */
    public Party() {
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
