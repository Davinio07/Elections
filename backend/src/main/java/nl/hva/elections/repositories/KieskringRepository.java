package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Kieskring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Kieskring entity.
 * The primary key is of type Integer.
 */
@Repository
public interface KieskringRepository extends JpaRepository<Kieskring, Integer> {

    /**
     * Checks if a Kieskring with the given name already exists.
     * Used by the DataInitializer.
     * @param name The name to check
     * @return true if an entry exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds all Kieskring entities and orders them by name ascending.
     *
     * @return a sorted list of Kieskring objects
     */
    List<Kieskring> findAllByOrderByNameAsc();

    // Note: findAll(), findById(), save(), etc. are
    // already included from JpaRepository.
}