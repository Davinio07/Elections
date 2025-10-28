// src/main/java/nl/hva/elections/repositories/CandidateRepository.java
package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // <-- Import this

@Repository // <-- ADD THIS ANNOTATION
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    boolean existsByPartyIdAndName(Integer partyId, String name);
}