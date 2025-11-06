// src/main/java/nl/hva/elections/repositories/CandidateRepository.java
package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    /** Checks if a candidate with this exact name already exists for this party. */
    boolean existsByNameAndPartyId(String name, Long partyId);

    /** Finds candidates based on both Party ID and Gender. */
    List<Candidate> findByPartyIdAndGender(Long partyId, String gender);

    /** Finds candidates based only on Party ID. */
    List<Candidate> findByPartyId(Long partyId);

    /** Finds candidates based only on Gender. */
    List<Candidate> findByGender(String gender);
}