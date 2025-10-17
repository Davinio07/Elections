package nl.hva.elections.repositories;

import nl.hva.elections.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User data.
 * By extending JpaRepository, we get a bunch of CRUD (Create, Read, Update, Delete)
 * methods for free, like save(), findById(), findAll(), etc.
 *
 * Spring will automatically implement this interface for us at runtime.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // We can add custom query methods here later if we need them.
    // For example: User findByUsername(String username);
}