package nl.hva.elections.repositories;

import nl.hva.elections.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User data.
 * methods like save(), findById(), findAll(), etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Methoden om te zoeken naar een gebruiker op basis van de gebruikersnaam of het e-mailadres.
    // Optional betekent dat het resultaat een gebruiker kan bevatten, of leeg kan zijn.
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}