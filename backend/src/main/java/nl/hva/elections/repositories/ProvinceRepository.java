package nl.hva.elections.repositories;

import nl.hva.elections.xml.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Province entity.
 * This interface automatically provides methods like save(), findAll(), count(), etc.
 *
 * We specify <Province, Integer> because:
 * 1. Province is the entity class this repository manages.
 * 2. Integer is the data type of the Province's primary key (province_id).
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    Optional<Province> findByName(String name);
    List<Province> findAll();
    // But for the DataSeeder, the built-in .count() and .saveAll() are all we need.
}