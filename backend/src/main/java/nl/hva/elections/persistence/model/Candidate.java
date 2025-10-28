package nl.hva.elections.persistence.model;

import jakarta.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer id;

    private String name;
    private String residence;
    private String gender;

    // This is the "owner" of the relationship
    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    public Candidate() {}

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

    public String getResidence() {
        return residence;
    }
    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public Party getParty() {
        return party;
    }
    public void setParty(Party party) {
        this.party = party;
    }
}