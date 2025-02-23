// AuditLogServiceTest.java
package ma.hahn.tickets;

import ma.hahn.tickets.entities.AuditLog;
import ma.hahn.tickets.repositories.AuditLogRepository;
import ma.hahn.tickets.services.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
        auditLog.setId(1L);
    }

    @Test
    void getAllLogs_ShouldReturnAllLogs() {
        // Arrange
        List<AuditLog> expectedLogs = Arrays.asList(new AuditLog(), new AuditLog());
        when(auditLogRepository.findAll()).thenReturn(expectedLogs);

        // Act
        List<AuditLog> result = auditLogService.getAllLogs();

        // Assert
        assertThat(result).isEqualTo(expectedLogs);
        verify(auditLogRepository, times(1)).findAll();
    }

    @Test
    void getLogById_WhenExists_ShouldReturnLog() {
        // Arrange
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));

        // Act
        AuditLog result = auditLogService.getLogById(1L);

        // Assert
        assertThat(result).isEqualTo(auditLog);
        verify(auditLogRepository, times(1)).findById(1L);
    }

    @Test
    void getLogById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(auditLogRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        AuditLog result = auditLogService.getLogById(2L);

        // Assert
        assertThat(result).isNull();
        verify(auditLogRepository, times(1)).findById(2L);
    }

    @Test
    void getLogsByTicketId_ShouldReturnLogs() {
        // Arrange
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByTicketId(anyLong())).thenReturn(expectedLogs);

        // Act
        List<AuditLog> result = auditLogService.getLogsByTicketId(1L);

        // Assert
        assertThat(result).isEqualTo(expectedLogs);
        verify(auditLogRepository, times(1)).findByTicketId(1L);
    }

    @Test
    void getLogsByUserId_ShouldReturnLogs() {
        // Arrange
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByUserId(anyLong())).thenReturn(expectedLogs);

        // Act
        List<AuditLog> result = auditLogService.getLogsByUserId(1L);

        // Assert
        assertThat(result).isEqualTo(expectedLogs);
        verify(auditLogRepository, times(1)).findByUserId(1L);
    }

    @Test
    void saveLog_ShouldReturnSavedLog() {
        // Arrange
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);

        // Act
        AuditLog result = auditLogService.saveLog(auditLog);

        // Assert
        assertThat(result).isEqualTo(auditLog);
        verify(auditLogRepository, times(1)).save(auditLog);
    }

    @Test
    void deleteLog_ShouldCallRepositoryDelete() {
        // Act
        auditLogService.deleteLog(1L);

        // Assert
        verify(auditLogRepository, times(1)).deleteById(1L);
    }
}

