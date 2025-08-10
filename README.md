CHAM - School Administrative Backend 
CHAM is a backend application designed to manage student data for educational institutions. It provides secure RESTful APIs for teachers to perform CRUD operations
on student records, manage notes, and ensure data integrity with JWT-based authentication. Built with Spring Boot and integrated with MySQL, CHAM offers a scalable and
robust solution for school administration.FeaturesStudent Management: APIs to create, read, update, and delete student records, including details like name, grade, age, language, 
parent name, and email.
Note Management: Endpoints to add and retrieve notes for individual students, enabling teachers to track progress or observations.
User Authentication: Secure JWT-based authentication to restrict access to authorized teachers, protecting sensitive student data.
Pagination: Supports paginated retrieval of student lists for efficient data handling.
Error Handling: Robust validation and error responses for unauthorized access, invalid inputs, and data conflicts (e.g., duplicate parent emails).
Database Integration: Efficient storage and retrieval of student and note data using MySQL with Spring Data JPA.

Tech Stack
Backend: Spring Boot 3.4.3, Spring Data JPA, Spring Security
Database: MySQL 8.4.0
Authentication: JSON Web Tokens (JWT) with JJWT 0.11.5
Language: Java 17
Tools: Maven, Lombok, Spring Boot DevTools
Testing: Spring Boot Starter Test

Setup InstructionsTo run the application locally:
Clone the repository: git clone https://github.com/0NGUTOR0/CHAM-by-SPRINGBOOT-
Set up MySQL: Ensure MySQL is running on localhost at port 3306. Create an empty database named cham_db using a MySQL client. Update the src/main/resources/application.properties file with your MySQL credentials, specifying the database URL as jdbc:mysql://localhost:3306/cham_db, your MySQL username, your MySQL password, and setting spring.jpa.hibernate.ddl-auto to update. Spring Data JPA automatically generates the database schema (e.g., tables for users, students, and notes) on startup.
Run the application: Execute mvn clean install followed by mvn spring-boot:run. The API will be accessible at http://localhost:8080.

API Endpoints
Authentication:
POST /cham/users/register - Register a new teacher, storing hashed credentials in the database.
POST /cham/users/login - Authenticate and receive a JWT token.

Student Management:
GET /cham/students?page={page}&limit={limit} - Retrieve a paginated list of students for the authenticated teacher.
POST /cham/students/addlearner - Create a new student with details like name, grade, birth year, language, parent name, and email.
PATCH /cham/students/{studentID} - Update a student’s name or email.
DELETE /cham/students/{studentID} - Delete a student.

Note Management:
POST /cham/students/notes/{studentID} - Add a note to a student.
GET /cham/students/notes/{studentID} - Retrieve all notes for a student.

Project Structure: 
src/main/java/com/example/cham: Core backend logic, including controllers (e.g., StudentController), services, and JPA entities for users, students, and notes.
src/main/resources: Configuration files, including application.properties for database and JWT settings.
src/test: Unit and integration tests to ensure API reliability.

Authentication: 
CHAM uses JWT-based authentication to secure API access:
Registration: Teachers sign up via /cham/usersregister, storing hashed credentials in a users table managed by JPA.
Login: Returns a JWT token via /cham/users/login for accessing protected endpoints.
Protected Endpoints: APIs like /cham/students and /cham/students/notes/{studentID} require a valid JWT in the Authorization header (e.g., Bearer <token>).

Future Enhancements: 
Add filtering and search capabilities for student records.
Implement role-based access control for additional user types (e.g., admins).
Integrate caching with Redis for improved API performance.

