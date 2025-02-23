package ma.hahn.tickets.controllers;

import ma.hahn.tickets.controllers.data.LoginRequest;
import ma.hahn.tickets.controllers.data.UserRequest;
import ma.hahn.tickets.entities.User;
import ma.hahn.tickets.entities.UserRole;
import ma.hahn.tickets.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody UserRequest request) {
        return userService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable UserRole role) {
        return userService.getUsersByRole(role);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public String getUserRole(@RequestBody LoginRequest loginRequest) {
        // Validate the user credentials and return the role
        String role = userService.getRoleByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword());
        if (role != null) {
            return role;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

}
