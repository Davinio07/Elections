    package nl.hva.elections.persistence.model;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.persistence.*;
    import java.util.List;

    @Entity
    public class Party {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "party_id")
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
         * The list of candidates belonging to this party.
         * The 'mappedBy' value must match the field name 'party' in the Candidate class.
         * @JsonManagedReference indicates this is the "owning" (forward) part of the relationship
         * for JSON serialization, ensuring the candidates are included but preventing a loop.
         */
        @OneToMany(mappedBy = "party", fetch = FetchType.LAZY)
        @JsonManagedReference // <-- NIEUW: Vertelt Jackson om deze lijst te serialiseren
        private List<Candidate> candidates;

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

        public List<Candidate> getCandidates() {
            return candidates;
        }
        public void setCandidates(List<Candidate> candidates) {
            this.candidates = candidates;
        }
    }
