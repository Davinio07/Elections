package nl.hva.elections.xml.model;

public class Candidate {
    private final String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String locality;
    private String partyName; // <--- ADD THIS FIELD

    public Candidate(String id) {
        this.id = id;
    }

    // getters and setters

    public String getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getLocality() {
        return locality;
    }
    public void setLocality(String locality) {
        this.locality = locality;
    }

    // <--- ADD GETTER AND SETTER FOR partyName
    public String getPartyName() {
        return partyName;
    }
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
}