package nl.hva.elections.repositories;

import nl.hva.elections.persistence.model.Kieskring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // <--- Import this

@Repository
public interface KieskringRepository extends JpaRepository<Kieskring, Integer> {

    boolean existsByName(String name);

    // --- ADD THIS METHOD ---
    Optional<Kieskring> findByName(String name);

    List<Kieskring> findAllByOrderByNameAsc();

    @Query("SELECT k FROM Kieskring k WHERE k.province.province_id = :provinceId")
    List<Kieskring> findByProvinceId(Integer provinceId);
}