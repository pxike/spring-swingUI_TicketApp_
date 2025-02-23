package ma.hahn.ticketsUi;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeUI {
    String username;
    public EmployeeUI(String username ,String role) {
        this.username = username;
        JFrame frame = new JFrame("Employee Ticket Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

// Display connected user
        JLabel userLabel = new JLabel("Connected as: " + username + " with Role: " + role);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        frame.add(userLabel, gbc);

// Ticket Creation Form components
        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(25); // Increased width of the text field
        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(5, 25); // Increased width of the text area
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        JLabel priorityLabel = new JLabel("Priority:");
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"NETWORK", "HARDWARE", "SOFTWARE", "OTHER"});
        JButton submitButton = new JButton("Submit Ticket");

// Layout setup for form components starting at gridy 1
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(titleLabel, gbc);
        gbc.gridx = 1;
        frame.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        frame.add(scrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        frame.add(priorityLabel, gbc);
        gbc.gridx = 1;
        frame.add(priorityCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        frame.add(categoryLabel, gbc);
        gbc.gridx = 1;
        frame.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        frame.add(submitButton, gbc);

// Table setup with empty data
        String[] columnNames = {"Ticket ID", "Title", "Description", "Status", "Priority"};
        JTable ticketTable = new JTable(new Object[][]{}, columnNames);

// Set preferred size for the table to make it wider
        ticketTable.setPreferredScrollableViewportSize(new Dimension(1000, 300)); // Match the IT Support Dashboard table size
        JScrollPane tableScrollPane = new JScrollPane(ticketTable);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.weighty = 1.0; // Allow vertical expansion
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        frame.add(tableScrollPane, gbc);

// Disconnect button
        JButton disconnectButton = new JButton("Disconnect");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        frame.add(disconnectButton, gbc);

// Set a larger size for the frame to accommodate the wider table
        frame.setSize(1200, 600); // Match the IT Support Dashboard frame size
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);

        disconnectButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(LoginUI::createUI);
        });

        // Fetch tickets asynchronously when the UI is loaded
        fetchAndUpdateTickets(ticketTable, columnNames, frame);

        // Submit ticket action
        submitButton.addActionListener(e -> {
            // Get values from the form fields
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String priority = priorityCombo.getSelectedItem().toString().toUpperCase();
            String category = categoryCombo.getSelectedItem().toString().toUpperCase();

            // Create a Map to represent the JSON payload
            Map<String, String> ticketData = new HashMap<>();
            ticketData.put("title", title);
            ticketData.put("description", description);
            ticketData.put("priority", priority);
            ticketData.put("category", category);

            // Convert the Map to JSON using Gson
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(ticketData);

            try {
                // Send POST request using RestAssured
                Response response = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .body(jsonPayload)
                        .post("http://localhost:8080/api/tickets?username="+username);
                // Check the response status code
                if (response.getStatusCode() == 200) {
                    // Show success message
                    JOptionPane.showMessageDialog(frame, "Ticket Created Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear the form fields
                    titleField.setText("");
                    descriptionArea.setText("");
                    priorityCombo.setSelectedIndex(0); // Reset to default
                    categoryCombo.setSelectedIndex(0); // Reset to default

                    // Refresh the ticket table
                    fetchAndUpdateTickets(ticketTable, columnNames, frame);
                } else {
                    // Show error message if the request was not successful
                    JOptionPane.showMessageDialog(frame, "Failed to create ticket. Server returned: " + response.getStatusCode(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                // Handle exceptions (e.g., network issues)
                JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.setSize(1200, 600); // Adjust width and height as needed
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void fetchAndUpdateTickets(JTable ticketTable, String[] columnNames, JFrame frame) {
        SwingWorker<Object[][], Void> worker = new SwingWorker<>() {
            @Override
            protected Object[][] doInBackground() {
                try {
                    // Replace URL with your API endpoint
                    Response response = RestAssured.get("http://localhost:8080/api/tickets/user?name=" + username);
                    JsonPath jsonPath = response.jsonPath();
                    List<Map<String, Object>> tickets = jsonPath.getList("$");
                    Object[][] data = new Object[tickets.size()][5]; // Change to 5 columns
                    for (int i = 0; i < tickets.size(); i++) {
                        Map<String, Object> ticket = tickets.get(i);
                        data[i][0] = ticket.get("id");
                        data[i][1] = ticket.get("title");
                        data[i][2] = ticket.get("description"); // Add description field
                        String status = (String) ticket.get("status");
                        status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                        data[i][3] = status;
                        String priority = (String) ticket.get("priority");
                        priority = priority.substring(0, 1).toUpperCase() + priority.substring(1).toLowerCase();
                        data[i][4] = priority;
                    }
                    return data;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new Object[0][5]; // Change to 5 columns
                }
            }

            @Override
            protected void done() {
                try {
                    Object[][] data = get();
                    ticketTable.setModel(new DefaultTableModel(data, columnNames));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error fetching tickets: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}