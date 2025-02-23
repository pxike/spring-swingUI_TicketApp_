package ma.hahn.tickets.repositories;

import ma.hahn.tickets.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
// TicketRepository.java
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBy(User user);
    List<Ticket> findByStatus(Status status);
    List<Ticket> findByPriority(Priority priority);
    List<Ticket> findByCategory(Category category);

    @Query("SELECT t FROM Ticket t WHERE t.title LIKE %:searchTitle% AND t.category = :category AND t.priority = :priority AND t.status = :status")
    List<Ticket> searchTickets(@Param("searchTitle") String searchTitle,
                               @Param("category") Category category,
                               @Param("priority") Priority priority,
                               @Param("status") Status status);}

