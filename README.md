# IT Support Ticket System

A ticket management system for tracking IT issues. Employees can submit and track tickets while IT staff manage and resolve them.

## Stack

- **Backend**: Java 17, Spring Boot
- **Frontend**: Java Swing with MigLayout
- **Database**: Oracle SQL
- **Docker**: Containers for backend and DB

## Features

### Tickets
- Title and description
- Priority (Low/Medium/High)
- Category (Network/Hardware/Software/Other)
- Status tracking (New → In Progress → Resolved)
- Comments and update history

### Users
- **Employees**: Create and view their tickets
- **IT Support**: Manage all tickets, update status, add notes

### Other
- Search by ticket ID
- Filter by status
- Audit logging
- REST API with Swagger docs

## Setup

1. Clone the repo:
```bash
git clone https://github.com/pxike/ticket-management-system.git
cd ticket-management-system
```

2. Run with Docker:
```bash
docker compose -f compose.yaml -p pr up -d
```
Note: Spring will retry automatically connecting to Oracle during startup. First startups might fail while Oracle initializes.

3. Launch the UI:
```bash
java -jar TicketUi-1.0-SNAPSHOT-jar-with-dependencies.jar
```

4. Access:
- API docs: http://localhost:8080/swagger-ui/
- Database: http://localhost:1521

## Project Structure

```
ticket-management-system/
├── Tickets/           # Backend
├── TicketsUI/         # Frontend
├── docker-compose.yml
└── schema.sql        # DB schema
├── TicketUi-1.0-SNAPSHOT-jar-with-dependencies.jar  # Executable UI
```

## Development

Run tests:
```bash
./mvnw test
```

## Support

Create an issue or email dev-support@company.com
