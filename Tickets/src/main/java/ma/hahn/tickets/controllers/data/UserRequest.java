package ma.hahn.tickets.controllers.data;

import lombok.Data;
import ma.hahn.tickets.entities.UserRole;

@Data
public class UserRequest {
    private String username;
    private String password;
    private UserRole role;
}
