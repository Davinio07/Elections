package nl.hva.elections.persistence.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore; // <--- ADD THIS IMPORT

@Entity
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;
    private String logoUrl;
    private Integer nationalSeats;

    // This links to the Candidate class
    // "mappedBy" tells JPA that the 'party' field in the Candidate class owns this relationship
    @OneToMany(mappedBy = "party")
    @JsonIgnore // <--- ADD THIS ANNOTATION
    private List<Candidate> candidates;

    // A no-argument constructor is required by JPA
    public Party() {}

    // A constructor to make it easy to create
    public Party(String name, String logoUrl, int nationalSeats) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.nationalSeats = nationalSeats;
    }

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLogoUrl() {
        return logoUrl;
    }
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    public Integer getNationalSeats() {
        return nationalSeats;
    }
    public void setNationalSeats(Integer nationalSeats) {
        this.nationalSeats = nationalSeats;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }
    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}