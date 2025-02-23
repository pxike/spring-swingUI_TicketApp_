package ma.hahn.tickets.controllers.data;

import lombok.Data;
import ma.hahn.tickets.entities.Category;
import ma.hahn.tickets.entities.Priority;

// Simple DTOs
@Data
public class TicketRequest {
    private String title;
    private String description;
    private Priority priority;
    private Category category;
}
