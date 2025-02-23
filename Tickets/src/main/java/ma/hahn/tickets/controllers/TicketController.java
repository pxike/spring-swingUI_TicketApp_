package ma.hahn.tickets.controllers;

import lombok.Data;
import ma.hahn.tickets.controllers.data.TicketRequest;
import ma.hahn.tickets.controllers.data.UserRequest;
import ma.hahn.tickets.entities.*;
import ma.hahn.tickets.services.TicketService;
import ma.hahn.tickets.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Simplified Controllers
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final UserService userService;

    @Autowired
    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @PostMapping
    public Ticket createTicket(@RequestBody TicketRequest request, @RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        return ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getCategory(),
                user.getId()
        );
    }

    @PutMapping("/{ticketId}/status")
    public Ticket updateStatus(@PathVariable Long ticketId, @RequestParam Status status, @RequestParam Long userId ,@RequestParam String comment) {
        return ticketService.updateTicketStatus(ticketId, status, userId, comment);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String priority,
            @RequestParam(required = false) String comment) {
        Ticket updatedTicket = ticketService.updateTicket(id, status, priority, comment);
        return ResponseEntity.ok(updatedTicket);
    }


    @GetMapping("/user/{userId}/")
    public List<Ticket> getUserTickets(@PathVariable Long userId) {
        return ticketService.getTicketsByUser(userId);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public List<Ticket> getUserTicketsByName(@RequestParam String name) {
        return ticketService.getTicketsByUser(userService.getUserByUsername(name).getId());
    }

    @GetMapping("/status/{status}")
    public List<Ticket> getTicketsByStatus(@PathVariable Status status) {
        return ticketService.getTicketsByStatus(status);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/search")
    public List<Ticket> searchTickets(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status) {
        return ticketService.searchTickets(title, category, priority, status);
    }
}

