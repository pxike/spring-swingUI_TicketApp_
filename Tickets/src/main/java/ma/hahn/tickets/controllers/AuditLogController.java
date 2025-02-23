package ma.hahn.tickets.controllers;

import ma.hahn.tickets.entities.AuditLog;
import ma.hahn.tickets.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getLogById(@PathVariable Long id) {
        AuditLog auditLog = auditLogService.getLogById(id);
        return auditLog != null ? ResponseEntity.ok(auditLog) : ResponseEntity.notFound().build();
    }

    @GetMapping("/ticket/{ticketId}")
    public List<AuditLog> getLogsByTicketId(@PathVariable Long ticketId) {
        return auditLogService.getLogsByTicketId(ticketId);
    }

    @GetMapping("/user/{userId}")
    public List<AuditLog> getLogsByUserId(@PathVariable Long userId) {
        return auditLogService.getLogsByUserId(userId);
    }

    @PostMapping
    public AuditLog createLog(@RequestBody AuditLog auditLog) {
        return auditLogService.saveLog(auditLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        auditLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}