package nl.hva.elections.repositories;

import nl.hva.elections.xml.model.Gemeente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Gemeente entity.
 */
@Repository
public interface GemeenteRepository extends JpaRepository<Gemeente, Integer> {

    /**
     * Finds all Gemeente entities that belong to a specific province.
     * Spring Data JPA will auto-generate this query based on the method name.
     */
    List<Gemeente> findByProvinceId(Integer provinceId);

    /**
     * Finds all Gemeente entities that belong to a specific kieskring.
     */
    List<Gemeente> findByKieskringId(Integer kieskringId);

    /**
     * Finds a Gemeente by its name.
     */
    Gemeente findByName(String name);

    /**
     * This is the key query for your new flow.
     * It uses the GEMEENTE table as a "bridge" to find all
     * KIESKRING IDs that are associated with a given PROVINCE ID.
     *
     * @param provinceId The ID of the province
     * @return A List of Kieskring IDs (Integer)
     */
    @Query("SELECT DISTINCT g.kieskring_id FROM Gemeente g WHERE g.province_id = :provinceId")
    List<Integer> findDistinctKieskringIdsByProvinceId(@Param("provinceId") Integer provinceId);

}