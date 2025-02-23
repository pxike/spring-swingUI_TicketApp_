package ma.hahn.tickets.repositories;

import ma.hahn.tickets.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// UserRepository.java
public interface UserRepository extends JpaRepository<User, Long> {
    void deleteUserById(Long id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRole(UserRole role);

}

