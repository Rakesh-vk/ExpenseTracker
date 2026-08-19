# Expense Tracker API

A secure REST API for managing personal expenses, built using Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, and MySQL.

The application supports user registration, JWT-based authentication, and complete expense CRUD operations. Each user's expenses are isolated so that an authenticated user can access only their own expenses.

---

## Features

### User Management

- User registration
- Email-based user lookup
- Duplicate email validation
- BCrypt password hashing
- User details management

### Authentication & Security

- JWT-based authentication
- Stateless authentication using Spring Security
- BCrypt password encryption
- Custom JWT authentication filter
- JWT token validation
- Protected API endpoints
- Public registration and login endpoints
- User-level expense authorization
- Unauthorized requests return `401 Unauthorized`

### Expense Management

- Create expense
- Get all expenses for the authenticated user
- Get expense by ID
- Update expense
- Delete expense
- Expense ownership validation
- Request validation
- Custom exception handling

### API Documentation

- Swagger UI
- OpenAPI documentation
- JWT Bearer authentication in Swagger
- Interactive API testing

### Testing

- Unit tests
- Service layer tests
- Repository tests
- Controller tests
- JWT authentication filter tests
- Authentication integration tests
- Expense integration tests
- Security and ownership tests

---

# Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.0 | Backend framework |
| Spring MVC | REST API |
| Spring Security | Authentication & authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Relational database |
| Bean Validation | Request validation |
| BCrypt | Password hashing |
| Lombok | Boilerplate reduction |
| Swagger / OpenAPI | API documentation |
| JUnit 5 | Testing |
| Mockito | Unit testing |
| MockMvc | REST API testing |
| Maven | Build & dependency management |

---

# Architecture

The application follows a layered architecture.

```text
                    Client
                      |
                      | HTTP Request
                      ↓
               ┌───────────────┐
               │   Controller  │
               └───────┬───────┘
                       |
                       ↓
               ┌───────────────┐
               │    Service    │
               └───────┬───────┘
                       |
                       ↓
               ┌───────────────┐
               │   Repository  │
               └───────┬───────┘
                       |
                       ↓
                  ┌─────────┐
                  │  MySQL  │
                  └─────────┘
