package nl.hva.elections.persistence.model;

import jakarta.persistence.*;

@Entity
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;
    private String logo_url;
    private Number national_seats;

    // This links to the Candidate class
    // "mappedBy" tells JPA that the 'party' field in the Candidate class owns this relationship
    @OneToMany(mappedBy = "party")
    private List<Candidate> candidates;

    // A no-argument constructor is required by JPA
    public Party() {}

    // A constructor to make it easy to create
    public Party(String name, String logoUrl, int nationalSeats) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.nationalSeats = nationalSeats;
    }

}
