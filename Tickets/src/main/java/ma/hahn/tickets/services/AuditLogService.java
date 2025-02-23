package ma.hahn.tickets.services;
import ma.hahn.tickets.entities.AuditLog;
import ma.hahn.tickets.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    public AuditLog getLogById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    public List<AuditLog> getLogsByTicketId(Long ticketId) {
        return auditLogRepository.findByTicketId(ticketId);
    }

    public List<AuditLog> getLogsByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public AuditLog saveLog(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public void deleteLog(Long id) {
        auditLogRepository.deleteById(id);
    }
}
