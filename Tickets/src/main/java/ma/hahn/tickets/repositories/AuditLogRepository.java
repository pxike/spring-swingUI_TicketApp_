package ma.hahn.tickets.repositories;

import ma.hahn.tickets.entities.ActionType;
import ma.hahn.tickets.entities.AuditLog;
import ma.hahn.tickets.entities.Ticket;
import ma.hahn.tickets.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// AuditLogRepository.java
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTicketOrderByTimestampDesc(Ticket ticket);
    List<AuditLog> findByUserOrderByTimestampDesc(User user);
    List<AuditLog> findByActionType(ActionType actionType);
    List<AuditLog> findByTicketId(Long ticketId);
    List<AuditLog> findByUserId(Long userId);

}
