package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Province entity.
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    /**
     * Checks if a Province with the given name already exists.
     * Used by the DataInitializer.
     * @param name The name to check
     * @return true if an entry exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a Province by its unique name.
     * Used by the DataInitializer.
     * @param name The name to find
     * @return an Optional containing the Province if found
     */
    Optional<Province> findByName(String name);

    /**
     * Finds all Province entities and orders them by name ascending.
     * This will be used by our new controller.
     *
     * @return a sorted list of Province objects
     */
    List<Province> findAllByOrderByNameAsc();
}