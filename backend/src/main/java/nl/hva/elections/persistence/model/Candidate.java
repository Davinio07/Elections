package nl.hva.elections.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer id;

    @Column(name = "import_id")
    private String xmlId;

    private String name; // Stores "FirstName LastName"
    private String residence;
    private String gender;

    @ManyToOne
    @JoinColumn(name = "party_id")
    @JsonBackReference
    private Party party;

    // This tells JPA: "Don't save this string to the database,
    // I'm just using it temporarily while parsing."
    @Transient
    private String tempPartyName;

    public Candidate() {
    }

    // The "Fusion" Constructor
    public Candidate(String xmlId, String firstName, String lastName, String gender, String locality, String partyName) {
        this.xmlId = xmlId;
        this.gender = gender;
        this.residence = locality;
        this.name = firstName + " " + lastName; // Fuse names here!
        this.tempPartyName = partyName;
    }

    // --- GETTERS & SETTERS ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getXmlId() { return xmlId; }
    public void setXmlId(String xmlId) { this.xmlId = xmlId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getResidence() { return residence; }
    public void setResidence(String residence) { this.residence = residence; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Party getParty() { return party; }
    public void setParty(Party party) { this.party = party; }

    // Helper getter for the parser to use later
    public String getTempPartyName() { return tempPartyName; }
    public void setTempPartyName(String tempPartyName) { this.tempPartyName = tempPartyName; }
}