package ma.hahn.ticketsUi;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class LoginUI {
    static void createUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(usernameLabel, gbc);
        gbc.gridx = 1;
        frame.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(passwordLabel, gbc);
        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        frame.add(loginButton, gbc);

        // Simulate role-based login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Add "encrypted" to the password
                password += "encrypted";

                // Prepare the JSON payload
                Map<String, Object> jsonPayload = new HashMap<>();
                jsonPayload.put("username", username);
                jsonPayload.put("password", password);
                String jsonBody = new Gson().toJson(jsonPayload);

                // Send POST request with RestAssured
                Response response = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .body(jsonBody)
                        .post("http://localhost:8080/api/users/login");
                // Get the response body as a string
                String userRole = response.getBody().asString().trim();

                // Handle the response based on role
                if ("EMPLOYEE".equals(userRole)) {
                    new EmployeeUI(username,userRole); // Show Employee UI
                    frame.dispose();
                } else if ("IT_SUPPORT".equals(userRole)) {
                    new ITSupportUI(username,userRole); // Show IT UI
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials!");
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
