package ma.hahn.ticketsUi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import javax.swing.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Base URI
        RestAssured.baseURI = "http://localhost:8080";

        // Create user 'it' with role IT_SUPPORT
        Response response1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"it\", \"password\": \"test\", \"role\": \"IT_SUPPORT\"}")
                .post("/api/users");
        System.out.println("Create account 'it': password: test ");

        // Create user 'emp1' with role EMPLOYEE
        Response response2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"emp1\", \"password\": \"test\", \"role\": \"EMPLOYEE\"}")
                .post("/api/users");
        System.out.println("Create account 'emp1': password: test ");

        // Create user 'emp2' with role EMPLOYEE
        Response response3 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"emp2\", \"password\": \"test\", \"role\": \"EMPLOYEE\"}")
                .post("/api/users");
        System.out.println("Create account 'emp2': password: test ");

        String[] users = {"emp1", "emp2"};

        // Possible titles, priorities, and categories
        String[] titles = {"Network bug", "Server down", "UI glitch", "Database issue", "Login failure"};
        String[] priorities = {"LOW", "MEDIUM", "HIGH"};
        String[] categories = {"SOFTWARE", "HARDWARE", "SECURITY", "NETWORK"};

        // Random object for generating random numbers
        Random random = new Random();

        // Loop through each user and create multiple diverse tickets
        for (String user : users) {
            // Create 5 tickets per user with varying details
            for (int i = 1; i <= 3; i++) {
                // Randomly pick values from the arrays
                String title = titles[random.nextInt(titles.length)];
                String priority = priorities[random.nextInt(priorities.length)];
                String category = categories[random.nextInt(categories.length)];

                // Create the ticket request body
                String ticketJson = "{\n" +
                        "    \"title\": \"" + title + " " + i + "\",\n" +
                        "    \"description\": \"" + title + " description for " + user + " " + i + "\",\n" +
                        "    \"priority\": \"" + priority + "\",\n" +
                        "    \"category\": \"" + category + "\"\n" +
                        "}";

                // Send the POST request to create the ticket
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(ticketJson)
                        .post("/api/tickets?username=" + user);

                // Print ticket creation response for each user
                System.out.println("Created ticket " + i + " for user '" + user + "' with status: " + response.getStatusCode());
            }
        }
        SwingUtilities.invokeLater(LoginUI::createUI);
    }
}
