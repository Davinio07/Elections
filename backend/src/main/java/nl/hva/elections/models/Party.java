package nl.hva.elections.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

@Entity
@XmlRootElement(name = "Party")
@XmlAccessorType(XmlAccessType.FIELD)
public class Party {

    public Party(String id, String name) {
        this.electionId = id;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Long id;

    @XmlElement(name = "ElectionId")
    private String electionId;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "TotalVotes")
    @JsonProperty("totalVotes")
    private int votes;

    @XmlElement(name = "NationalSeats")
    @JsonProperty("nationalSeats")
    private int seats;

    @XmlElement(name = "VotePercentage")
    @JsonProperty("votePercentage")
    private double percentage;

    /** Default constructor for JPA and JAXB. */
    public Party() {}

    // Optionally keep a constructor for XML-based creation
    public Party(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    @Override
    public String toString() {
        return "Party{" +
                "id=" + id +
                ", electionId='" + electionId + '\'' +
                ", name='" + name + '\'' +
                ", votes=" + votes +
                ", seats=" + seats +
                ", percentage=" + percentage +
                '}';
    }
}
