package ma.hahn.tickets;

import ma.hahn.tickets.entities.User;
import ma.hahn.tickets.entities.UserRole;
import ma.hahn.tickets.repositories.UserRepository;
import ma.hahn.tickets.services.UserService;
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

@ExtendWith(MockitoExtension.class)  // Make sure this is present to initialize mocks
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("testpasswordencrypted");
        testUser.setRole(UserRole.EMPLOYEE);
    }

    @Test
    void testCreateUser_success() {
        // Arrange
        String username = "testuser";
        String password = "password";
        UserRole role = UserRole.EMPLOYEE;
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(username, password, role);

        // Assert
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertTrue(createdUser.getPassword().contains("encrypted"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_usernameExists() {
        // Arrange
        String username = "testuser";
        String password = "password";
        UserRole role = UserRole.EMPLOYEE;
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(username, password, role);
        });
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_userFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserById(userId);

        // Assert
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    void testGetUserById_userNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUsersByRole() {
        // Arrange
        UserRole role = UserRole.EMPLOYEE;
        when(userRepository.findByRole(role)).thenReturn(List.of(testUser));

        // Act
        List<User> users = userService.getUsersByRole(role);

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(role, users.get(0).getRole());
    }

    @Test
    void testGetRoleByUsernameAndPassword_success() {
        // Arrange
        String username = "testuser";
        String password = "testpasswordencrypted";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        String role = userService.getRoleByUsernameAndPassword(username, password);

        // Assert
        assertNotNull(role);
        assertEquals("EMPLOYEE", role);
    }

    @Test
    void testGetRoleByUsernameAndPassword_invalidPassword() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        String role = userService.getRoleByUsernameAndPassword(username, password);

        // Assert
        assertNull(role);
    }

    @Test
    void testGetUserByUsername_userFound() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserByUsername(username);

        // Assert
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }


}
