package ma.hahn.ticketsUi;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class ITSupportUI {
    private final String username;

    public ITSupportUI(String username, String role) {
        this.username = username;
        JFrame frame = new JFrame("IT Support Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Top Panel: User Info and Ticket Filters
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.insets = new Insets(2, 2, 2, 2);
        topGbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Connected as: " + username + " with Role: " + role);
        topGbc.gridx = 0;
        topGbc.gridy = 0;
        topGbc.gridwidth = 7;
        topPanel.add(userLabel, topGbc);

        // Ticket Filters
        topGbc.gridwidth = 1;
        topGbc.gridy = 1;

        JLabel filterStatusLabel = new JLabel("Filter by Status:");
        JComboBox<String> filterStatusCombo = new JComboBox<>(new String[]{"ALL", "NEW", "IN_PROGRESS", "RESOLVED"});
        filterStatusCombo.setPreferredSize(new Dimension(150, 25));

        JLabel filterPriorityLabel = new JLabel("Filter by Priority:");
        JComboBox<String> filterPriorityCombo = new JComboBox<>(new String[]{"ALL", "LOW", "MEDIUM", "HIGH"});
        filterPriorityCombo.setPreferredSize(new Dimension(150, 25));

        JLabel searchUserLabel = new JLabel("Search by Username:");
        JTextField searchUserField = new JTextField(15);

        JButton filterButton = new JButton("Filter Tickets");

        // Add ticket filters
        topGbc.gridx = 0;
        topPanel.add(filterStatusLabel, topGbc);
        topGbc.gridx = 1;
        topPanel.add(filterStatusCombo, topGbc);
        topGbc.gridx = 2;
        topPanel.add(filterPriorityLabel, topGbc);
        topGbc.gridx = 3;
        topPanel.add(filterPriorityCombo, topGbc);
        topGbc.gridx = 4;
        topPanel.add(searchUserLabel, topGbc);
        topGbc.gridx = 5;
        topGbc.weightx = 1.0;
        topPanel.add(searchUserField, topGbc);
        topGbc.gridx = 6;
        topGbc.weightx = 0.0;
        topPanel.add(filterButton, topGbc);

        // Add topPanel to main frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        frame.add(topPanel, gbc);

        // Tickets Table
        String[] columnNames = {"Ticket ID", "Title", "Description", "Creation Date", "Status", "Priority", "Username"};
        final Object[][][] data = {fetchTicketsFromAPI()};

        DefaultTableModel model = new DefaultTableModel(data[0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column < 6 && column > 0;
            }
        };

        JTable ticketTable = new JTable(model);
        ticketTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        ticketTable.setRowSorter(sorter);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"NEW", "IN_PROGRESS", "RESOLVED"});
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});

        ticketTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusCombo)); // Status column
        ticketTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(priorityCombo)); // Priority column
        JScrollPane tableScrollPane = new JScrollPane(ticketTable);
        tableScrollPane.setPreferredSize(new Dimension(1000, 200)); // Reduced height

        JPanel ticketsPanel = new JPanel(new BorderLayout());
        ticketsPanel.add(new JLabel("Tickets"), BorderLayout.NORTH);
        ticketsPanel.add(tableScrollPane, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 0.3; // Reduced space for tickets
        frame.add(ticketsPanel, gbc);

        // Audit Log Filters Panel
        JPanel auditFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        auditFilterPanel.setBorder(BorderFactory.createTitledBorder("Audit Log Filters"));

        JLabel auditIdLabel = new JLabel("Ticket ID:");
        JTextField auditIdField = new JTextField(10);

        JButton applyAuditFilter = new JButton("Apply Filters");

        auditFilterPanel.add(auditIdLabel);
        auditFilterPanel.add(auditIdField);
        auditFilterPanel.add(applyAuditFilter);

        gbc.gridy = 2;
        gbc.weighty = 0.0; // No extra vertical space for filter panel
        frame.add(auditFilterPanel, gbc);

        // Audit Log Table
        String[] columnNamesLogs = {"Ticket ID", "Employee ID", "Action Type", "Timestamp ", "Comment", "Old Status", "New Status"};
        Object[][] dataLog = fetchAuditLogsFromAPI();
        DefaultTableModel modellog = new DefaultTableModel(dataLog, columnNamesLogs) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make audit log non-editable
            }
        };

        JTable AuditLogTable = new JTable(modellog);
        AuditLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableRowSorter<DefaultTableModel> sorterlog = new TableRowSorter<>(modellog);
        AuditLogTable.setRowSorter(sorterlog);

        JScrollPane tablelogScrollPane = new JScrollPane(AuditLogTable);
        tablelogScrollPane.setPreferredSize(new Dimension(1000, 150)); // Reduced height

        JPanel auditLogPanel = new JPanel(new BorderLayout());
        auditLogPanel.add(new JLabel("Audit Logs"), BorderLayout.NORTH);
        auditLogPanel.add(tablelogScrollPane, BorderLayout.CENTER);

        gbc.gridy = 3;
        gbc.weighty = 0.3; // Remaining space for audit logs
        frame.add(auditLogPanel, gbc);

        // Bottom Panel: Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton saveChanges = new JButton("Save Changes");
        JButton disconnectButton = new JButton("Disconnect");
        bottomPanel.add(saveChanges);
        bottomPanel.add(disconnectButton);

        gbc.gridy = 4;
        gbc.weighty = 0.0;
        frame.add(bottomPanel, gbc);

        // Finalize frame
        frame.setMinimumSize(new Dimension(1000, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Action Listeners
        disconnectButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(LoginUI::createUI);
        });

        saveChanges.addActionListener(e -> {
            JTextArea commentArea = new JTextArea(5, 20);
            commentArea.setLineWrap(true);
            JScrollPane commentScrollPane = new JScrollPane(commentArea);

            int option = JOptionPane.showConfirmDialog(
                    frame,
                    commentScrollPane,
                    "Enter a Comment",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String comment = commentArea.getText().trim();
                for (int i = 0; i < ticketTable.getRowCount(); i++) {
                    String ticketId = (String) ticketTable.getValueAt(i, 0);
                    String currentStatus = (String) ticketTable.getValueAt(i, 4);
                    String currentPriority = (String) ticketTable.getValueAt(i, 5);
                    String originalStatus = (String) data[0][i][4];
                    String originalPriority = (String) data[0][i][5];

                    if (!currentStatus.equals(originalStatus) || !currentPriority.equals(originalPriority)) {
                        RestAssured.baseURI = "http://localhost:8080/api/tickets";
                        Response response = given()
                                .queryParam("status", currentStatus)
                                .queryParam("priority", currentPriority)
                                .queryParam("comment", comment)
                                .when()
                                .put("/update/" + ticketId)
                                .then()
                                .statusCode(200)
                                .extract().response();
                        data[0][i][4] = currentStatus;
                        data[0][i][5] = currentPriority;
                    }
                }

                JOptionPane.showMessageDialog(frame, "Changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                Object[][] newAuditLogs = fetchAuditLogsFromAPI();
                DefaultTableModel auditLogModel = (DefaultTableModel) AuditLogTable.getModel();
                auditLogModel.setDataVector(newAuditLogs, columnNamesLogs);

            }
        });

        filterButton.addActionListener(e -> {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            String selectedStatus = (String) filterStatusCombo.getSelectedItem();
            if (!"ALL".equals(selectedStatus)) {
                filters.add(RowFilter.regexFilter("^" + Pattern.quote(selectedStatus) + "$", 4));
            }

            String selectedPriority = (String) filterPriorityCombo.getSelectedItem();
            if (!"ALL".equals(selectedPriority)) {
                filters.add(RowFilter.regexFilter("^" + Pattern.quote(selectedPriority) + "$", 5));
            }

            String employeename = searchUserField.getText().trim();
            if (!employeename.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(employeename), 6));
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        });

        applyAuditFilter.addActionListener(e -> {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            String ticketId = auditIdField.getText().trim();
            if (!ticketId.isEmpty()) {
                filters.add(RowFilter.regexFilter("^" + Pattern.quote(ticketId) + "$", 0));
            }

            if (filters.isEmpty()) {
                sorterlog.setRowFilter(null);
            } else {
                sorterlog.setRowFilter(RowFilter.andFilter(filters));
            }
        });
    }

    private Object[][] fetchTicketsFromAPI() {
        Response response = RestAssured.get("http://localhost:8080/api/tickets");
        String jsonResponse = response.getBody().asString();

        Gson gson = new Gson();
        Type ticketListType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> tickets = gson.fromJson(jsonResponse, ticketListType);

        Object[][] data = new Object[tickets.size()][7];
        for (int i = 0; i < tickets.size(); i++) {
            Map<String, Object> ticket = tickets.get(i);
            data[i][0] = String.valueOf(ticket.get("id")).replaceAll("\\.0$", "");
            data[i][1] = ticket.get("title");
            data[i][2] = ticket.get("description");
            List<Double> creationDate = (List<Double>) ticket.get("creationDate");
            int year = creationDate.get(0).intValue();
            int month = creationDate.get(1).intValue();
            int day = creationDate.get(2).intValue();
            int hour = creationDate.get(3).intValue();
            int minute = creationDate.get(4).intValue();
            int second = creationDate.get(5).intValue();
            LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            data[i][3] = dateTime.format(formatter);
            data[i][4] = ticket.get("status");
            data[i][5] = ticket.get("priority");
            data[i][6] = ((Map<String, Object>) ticket.get("createdBy")).get("username");
        }
        return data;
    }

    private Object[][] fetchAuditLogsFromAPI() {
        // Fetch audit logs from the API
        Response response = RestAssured.get("http://localhost:8080/api/audit-logs");
        String jsonResponse = response.getBody().asString();

        // Use Gson to parse the JSON response
        Gson gson = new Gson();
        Type auditLogListType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> auditLogs = gson.fromJson(jsonResponse, auditLogListType);

        // Convert the list of audit logs to a 2D array for the table
        Object[][] data = new Object[auditLogs.size()][7]; // 7 columns: Ticket ID, Employee ID, Action Type, Timestamp, Comment, Old Status, New Status
        for (int i = 0; i < auditLogs.size(); i++) {
            Map<String, Object> log = auditLogs.get(i);
            Map<String, Object> ticket = (Map<String, Object>) log.get("ticket");
            Map<String, Object> user = (Map<String, Object>) log.get("user");

            data[i][0] = String.valueOf(ticket.get("id")).replaceAll("\\.0$", "");; // Ticket ID
            data[i][1] = user.get("username"); // Employee ID (username)
            data[i][2] = log.get("actionType"); // Action Type
            List<Double> timestamp = (List<Double>) log.get("timestamp");
            int year = timestamp.get(0).intValue();
            int month = timestamp.get(1).intValue();
            int day = timestamp.get(2).intValue();
            int hour = timestamp.get(3).intValue();
            int minute = timestamp.get(4).intValue();
            int second = timestamp.get(5).intValue();
            LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            data[i][3] = dateTime.format(formatter);
            data[i][4] = log.get("commentText"); // Comment
            data[i][5] = log.get("oldStatus"); // Old Status
            data[i][6] = log.get("newStatus"); // New Status
        }

        return data;
    }
}