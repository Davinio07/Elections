package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface PartyRepository extends JpaRepository<Party, Integer> {

    Optional<Party> findByName(String name);

    // - save(Party party) -> Saves a new party or updates an existing one
    // - findById(Integer id) -> Finds one party by its ID
    // - findAll() -> Gets a List<Party> of all parties
    // - delete(Party party) -> Deletes a party
    // - count() -> Returns how many parties are in the table
}