package ma.hahn.tickets.services;

import ma.hahn.tickets.entities.*;
import ma.hahn.tickets.repositories.AuditLogRepository;
import ma.hahn.tickets.repositories.TicketRepository;
import ma.hahn.tickets.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         AuditLogRepository auditLogRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Ticket createTicket(String title, String description, Priority priority,
                               Category category, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setCategory(category);
        ticket.setCreatedBy(user);
        ticket.setStatus(Status.NEW);

        ticket = ticketRepository.save(ticket);

        // Create audit log for ticket creation
        createAuditLog(ticket, user, ActionType.STATUS_CHANGE, null, Status.NEW, null);

        return ticket;
    }

    public Ticket updateTicketStatus(Long ticketId, Status newStatus, Long userId , String comment) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != UserRole.IT_SUPPORT) {
            throw new RuntimeException("User does not have ROLE_IT_SUPPORT");
        }
        Status oldStatus = Status.valueOf(ticket.getStatus());
        ticket.setStatus(newStatus);
        ticket = ticketRepository.save(ticket);

        // Create audit log for status change
        createAuditLog(ticket, user, ActionType.STATUS_CHANGE, oldStatus, newStatus, comment);

        return ticket;
    }

    public void addComment(Long ticketId, Long userId, String comment) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        createAuditLog(ticket, user, ActionType.COMMENT, null, null, comment);
    }

    public Ticket updateTicket(Long id, String status, String priority, String comment) {
        // Find the ticket by ID
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        // Update fields
        String OldStatus = ticket.getStatus();
        ticket.setStatus(Status.valueOf(status));
        ticket.setPriority(Priority.valueOf(priority));
        createAuditLog(ticket, ticket.getCreatedBy(), ActionType.STATUS_CHANGE, Status.valueOf(OldStatus), Status.valueOf(status), comment);
        // Save the updated ticket
        return ticketRepository.save(ticket);
    }

    private void createAuditLog(Ticket ticket, User user, ActionType actionType,
                                Status oldStatus, Status newStatus, String comment) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTicket(ticket);
        auditLog.setUser(user);
        auditLog.setActionType(actionType);
        auditLog.setOldStatus(oldStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setCommentText(comment);
        auditLogRepository.save(auditLog);
    }

    public List<Ticket> getTicketsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByCreatedBy(user);
    }

    public List<Ticket> getTicketsByStatus(Status status) {
        return ticketRepository.findByStatus(status);
    }

    public List<AuditLog> getTicketHistory(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return auditLogRepository.findByTicketOrderByTimestampDesc(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> searchTickets(String title, Category category, Priority priority, Status status) {
        // Normalize inputs
        String searchTitle = (title != null && !title.isBlank()) ? title.trim() : null;

        // Delegate to repository
        return ticketRepository.searchTickets(
                searchTitle,
                category,
                priority,
                status
        );
    }
    public void deleteTicket(Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }
}

