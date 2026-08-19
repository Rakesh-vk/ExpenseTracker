
# Expense Tracker API

A secure REST API for managing personal expenses, built using Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, and MySQL.

## Features

- User registration
- BCrypt password hashing
- JWT-based authentication
- Stateless Spring Security
- Custom JWT authentication filter
- Expense CRUD operations
- User-level expense ownership
- Request validation
- Custom exception handling
- Swagger/OpenAPI documentation
- JWT authentication in Swagger
- Unit tests
- Repository tests
- Controller tests
- Integration tests
- Security tests

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.0 | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Database |
| Bean Validation | Request validation |
| BCrypt | Password hashing |
| Lombok | Boilerplate reduction |
| Swagger / OpenAPI | API documentation |
| JUnit 5 | Testing |
| Mockito | Unit testing |
| MockMvc | REST API testing |
| Maven | Build and dependency management |

## Architecture

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
MySQL

For authenticated requests:
Client
  |
  | Authorization: Bearer <JWT>
  v
JwtAuthenticationFilter
  |
  v
JwtService
  |
  | Validate JWT
  v
UserDetailsService
  |
  | Load User
  v
SecurityContext
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
MySQL

Authentication Flow
1. Register
POST /user/register

Example:

{
  "username": "rakesh",
  "email": "rakesh@example.com",
  "password": "password123"
}

The password is hashed using BCrypt before being stored.

2. Login
POST /auth/login

Example:

{
  "email": "rakesh@example.com",
  "password": "password123"
}

Example response:

{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
3. Access Protected APIs

Send the JWT using:

Authorization: Bearer <JWT>
Expense Ownership

Each expense belongs to a specific user.

The application does not trust a userId supplied by the client.

The authenticated user is obtained from the JWT:

JWT
 |
 v
Email
 |
 v
Authenticated User
 |
 v
Expense Ownership

The service layer uses user-specific repository methods:

findByIdAndUser(id, currentUser)

and:

findAllByUser(currentUser)

Therefore, a user cannot access another user's expenses.

API Endpoints
User Management
Method	Endpoint	Authentication
POST	/user/register	Public
Authentication
Method	Endpoint	Authentication
POST	/auth/login	Public
Expenses
Method	Endpoint	Authentication
GET	/Expense	JWT Required
GET	/Expense/{id}	JWT Required
POST	/Expense	JWT Required
PUT	/Expense/{id}	JWT Required
DELETE	/Expense/{id}	JWT Required
Expense Examples
Create Expense
POST /Expense
Authorization: Bearer <JWT>
Content-Type: application/json
{
  "spendOn": "Food",
  "amount": 500
}
Get All Expenses
GET /Expense
Authorization: Bearer <JWT>
Get Expense
GET /Expense/1
Authorization: Bearer <JWT>
Update Expense
PUT /Expense/1
Authorization: Bearer <JWT>
Content-Type: application/json
{
  "spendOn": "Shopping",
  "amount": 1000
}
Delete Expense
DELETE /Expense/1
Authorization: Bearer <JWT>
Validation

The API validates expense requests.

Missing Amount
{
  "spendOn": "Food"
}

Returns:

400 Bad Request
Amount is required
Negative Amount
{
  "spendOn": "Food",
  "amount": -500
}

Returns:

400 Bad Request
Amount must be greater than zero
Blank Spend Description
{
  "spendOn": "",
  "amount": 500
}

Returns:

400 Bad Request
Spend on is required
Exception Handling

The application uses custom exceptions including:

InvalidCredentialsException
ExpenseNotFound
UserAlreadyExistsException

Example:

{
  "status": 404,
  "message": "Expense not found with id 10",
  "timestamp": "..."
}
Swagger / OpenAPI

Swagger UI:

http://localhost:8082/swagger-ui/index.html

Swagger provides:

API documentation
Interactive API testing
JWT Bearer authentication
Protected endpoint testing
Using JWT in Swagger
Register a user using /user/register.
Login using /auth/login.
Copy the returned JWT.
Click Authorize.
Select bearerAuth.
Paste only the JWT.
Click Authorize.
Execute the protected Expense APIs.

Do not manually type Bearer.

Swagger automatically sends:

Authorization: Bearer <JWT>
Database

The application uses MySQL.

Relationship:

User
 |
 | 1
 |
 | *
 v
Expense

A user can have multiple expenses.

Each expense belongs to one user.

Configuration

Database configuration is stored in:

src/main/resources/application.properties

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

Do not commit real passwords or JWT secrets to Git.

Prerequisites
Java 21
MySQL
Git

The project includes Maven Wrapper, so Maven does not need to be installed separately.

Check Java:

java -version
Running the Application

Clone the repository:

git clone <YOUR_GITHUB_REPOSITORY_URL>

Enter the project:

cd ExpenseTracker

Run:

./mvnw spring-boot:run

Windows:

mvnw.cmd spring-boot:run
Running Tests

Run the complete test suite:

./mvnw clean test

Windows:

mvnw.cmd clean test

The test suite includes:

Service unit tests
Repository tests
Controller tests
JWT filter tests
Authentication tests
Integration tests
Security tests
Expense ownership tests

Expected result:

BUILD SUCCESS
Testing Strategy
Unit Tests

Mockito is used to isolate service and security components.

Examples:

ExpenseServiceImplTest
AuthenticationServiceImplTest
UserServiceImplTest
JwtAuthenticationFilterTest
Repository Tests

Repository behavior is tested independently.

ExpenseRepositoryTest
Controller Tests

MockMvc is used to test REST endpoints.

ExpenseControllerTest
AuthenticationControllerTest
UserControllerTest
Integration Tests

Integration tests load the Spring application context and verify multiple layers together.

AuthenticationIntegrationTest
ExpenseIntegrationTest
Security Tests

Security tests verify:

Valid JWT authentication
Missing JWT
Invalid JWT
Expired/invalid JWT
Protected endpoints
Unauthorized requests
User-specific expense access
Expense ownership
Prevention of cross-user access
Security Design

The application uses:

SessionCreationPolicy.STATELESS

Registration and login are public.

All other endpoints require authentication.

Unauthorized Request
Request
  |
  v
JWT Filter
  |
  v
No valid authentication
  |
  v
Spring Security
  |
  v
401 Unauthorized
Unauthorized Expense Access
JWT
 |
 v
Authenticated User
 |
 v
findByIdAndUser(id, currentUser)
 |
 v
Expense does not belong to user
 |
 v
ExpenseNotFound
 |
 v
404 Not Found
Project Structure
ExpenseTracker/
|
+-- src/
|   |
|   +-- main/
|   |   |
|   |   +-- java/
|   |       |
|   |       +-- com/rakesh/ExpenseTracker/
|   |           |
|   |           +-- config/
|   |           |   +-- SecurityConfig.java
|   |           |   +-- OpenApiConfig.java
|   |           |
|   |           +-- controller/
|   |           |   +-- AuthenticationController.java
|   |           |   +-- ExpenseController.java
|   |           |   +-- UserController.java
|   |           |
|   |           +-- dto/
|   |           |   +-- ExpenseRequestDTO.java
|   |           |   +-- ExpenseResponseDTO.java
|   |           |   +-- LoginRequestDTO.java
|   |           |   +-- LoginResponseDTO.java
|   |           |
|   |           +-- entity/
|   |           |   +-- Expense.java
|   |           |   +-- User.java
|   |           |
|   |           +-- exception/
|   |           |   +-- ExpenseNotFound.java
|   |           |   +-- InvalidCredentialsException.java
|   |           |   +-- UserAlreadyExistsException.java
|   |           |
|   |           +-- repository/
|   |           |   +-- ExpenseRepository.java
|   |           |   +-- UserRepository.java
|   |           |
|   |           +-- security/
|   |           |   +-- JwtAuthenticationFilter.java
|   |           |   +-- UserDetailsServiceImpl.java
|   |           |
|   |           +-- service/
|   |               +-- AuthenticationService.java
|   |               +-- AuthenticationServiceImpl.java
|   |               +-- ExpenseService.java
|   |               +-- ExpenseServiceImpl.java
|   |               +-- JwtService.java
|   |               +-- UserService.java
|   |               +-- UserServiceImpl.java
|   |
|   +-- test/
|       +-- java/
|           +-- com/rakesh/ExpenseTracker/
|
+-- pom.xml
+-- mvnw
+-- mvnw.cmd
+-- README.md
Design Decisions
Stateless Authentication

JWT is used instead of server-side HTTP sessions.

SessionCreationPolicy.STATELESS
Password Security

Passwords are never stored as plain text.

Plain Password
      |
      v
    BCrypt
      |
      v
Hashed Password
      |
      v
   Database
User-Level Authorization

The API derives the current user from the authenticated security context instead of accepting a user ID from the client.

This prevents a client from changing a user identifier to access another user's data.

Future Improvements

The following features are potential future enhancements and are not currently implemented:

Refresh tokens
Role-based authorization
Pagination
Expense categories
Expense filtering
Expense search
Monthly expense summaries
Budget management
Liquibase database migrations
Docker
CI/CD pipeline
Cloud deployment
Production secret management
Rate limiting
Monitoring and metrics
Author

Rakesh

Java Backend Developer

Technologies
Java
Spring Boot
Spring Security
JWT
Spring Data JPA
Hibernate
MySQL
REST APIs
JUnit
Mockito
Swagger / OpenAPI
License

This project is created for learning, portfolio, and interview purposes.
