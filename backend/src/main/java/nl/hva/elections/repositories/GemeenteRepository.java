package nl.hva.elections.repositories;

import nl.hva.elections.models.Gemeente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GemeenteRepository extends JpaRepository<Gemeente, Integer> {

    Optional<Gemeente> findByName(String name);

    List<Gemeente> findAllByOrderByNameAsc();

    // Finds all municipalities belonging to a specific kieskring ID
    @Query("SELECT g FROM Gemeente g WHERE g.kieskring.kieskring_id = :kieskringId")
    List<Gemeente> findByKieskringId(Integer kieskringId);
}