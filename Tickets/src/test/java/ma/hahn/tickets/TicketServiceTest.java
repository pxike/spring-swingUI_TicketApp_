package ma.hahn.tickets;

import ma.hahn.tickets.entities.*;
import ma.hahn.tickets.repositories.AuditLogRepository;
import ma.hahn.tickets.repositories.TicketRepository;
import ma.hahn.tickets.repositories.UserRepository;
import ma.hahn.tickets.services.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private TicketService ticketService;

    private User testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.IT_SUPPORT);

        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("Test Ticket");
        testTicket.setDescription("Test description");
        testTicket.setPriority(Priority.HIGH);
        testTicket.setCategory(Category.SOFTWARE);
        testTicket.setCreatedBy(testUser);
        testTicket.setStatus(Status.NEW);
    }

    @Test
    void testCreateTicket_success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        // Act
        Ticket createdTicket = ticketService.createTicket(
                "Test Ticket", "Test description", Priority.HIGH, Category.SOFTWARE, 1L);

        // Assert
        assertNotNull(createdTicket);
        assertEquals("Test Ticket", createdTicket.getTitle());
        assertEquals(Status.NEW.toString(), createdTicket.getStatus().toString());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testCreateTicket_userNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.createTicket("Test Ticket", "Test description", Priority.HIGH, Category.SOFTWARE, 1L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateTicketStatus_success() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        // Act
        Ticket updatedTicket = ticketService.updateTicketStatus(1L, Status.IN_PROGRESS, 1L, "IN_PROGRESS");

        // Assert
        assertNotNull(updatedTicket);
        assertEquals(Status.IN_PROGRESS.toString(), updatedTicket.getStatus().toString());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testUpdateTicketStatus_userNotFound() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicketStatus(1L, Status.IN_PROGRESS, 1L, "In Progress");
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateTicketStatus_insufficientPermissions() {
        // Arrange
        testUser.setRole(UserRole.EMPLOYEE);  // Change to non-IT_SUPPORT user
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicketStatus(1L, Status.IN_PROGRESS, 1L, "In Progress");
        });
        assertEquals("User does not have ROLE_IT_SUPPORT", exception.getMessage());
    }

    @Test
    void testGetTicketsByUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(ticketRepository.findByCreatedBy(testUser)).thenReturn(List.of(testTicket));

        // Act
        List<Ticket> tickets = ticketService.getTicketsByUser(1L);

        // Assert
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        assertEquals(testTicket, tickets.get(0));
    }

    @Test
    void testGetTicketsByStatus() {
        // Arrange
        when(ticketRepository.findByStatus(Status.NEW)).thenReturn(List.of(testTicket));

        // Act
        List<Ticket> tickets = ticketService.getTicketsByStatus(Status.NEW);

        // Assert
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        assertEquals(Status.NEW.toString(), tickets.get(0).getStatus().toString());
    }

    @Test
    void testGetTicketHistory() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(auditLogRepository.findByTicketOrderByTimestampDesc(testTicket))
                .thenReturn(List.of(new AuditLog()));

        // Act
        List<AuditLog> history = ticketService.getTicketHistory(1L);

        // Assert
        assertNotNull(history);
        assertEquals(1, history.size());
    }
}