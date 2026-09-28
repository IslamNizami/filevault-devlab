# FileVault - Secure File Upload and Metadata API

FileVault is a robust and secure Spring Boot backend application designed for handling file uploads, metadata storage, and protected downloads. It emphasizes security best practices such as file extension validation, safe storage (outside static folders), UUID renaming, and ownership-based access control.

## 🚀 Features

### Core Features
*   **Secure File Upload:** Multipart file upload with validation for allowed extensions and size limits.
*   **Safe Storage:** Files are stored on the local file system using UUIDs to prevent malicious payloads, naming collisions, and path traversal attacks.
*   **Metadata Management:** File details (original name, size, type, owner, etc.) are securely tracked in a PostgreSQL database.
*   **Paginated Listing:** Users can view a paginated list of their own uploaded files.
*   **Protected Downloads:** Files can only be downloaded by their rightful owners (IDOR protection).
*   **Global Exception Handling:** Clean, standardized JSON responses for all errors and exceptions.

### Bonus / Advanced Features
*   **SHA-256 Checksum:** Automatically calculates and stores the file's SHA-256 hash to ensure data integrity.
*   **Download Audit Logging:** Tracks and records every download attempt (who, what, and when) in the database.
*   **Scheduled Cleanup:** A nightly cron job (`@Scheduled`) that automatically sweeps the storage directory and deletes orphaned files (files that exist on the disk but have no corresponding database record).

## 🛠️ Tech Stack
*   **Java 17+**
*   **Spring Boot 3.x** (Web, Data JPA, Validation)
*   **PostgreSQL** (Relational Database)
*   **Maven** (Dependency Management)

## ⚙️ Prerequisites
*   Java Development Kit (JDK) 17 or higher
*   PostgreSQL running on `localhost:5432`
*   Maven installed

## 🔧 Configuration

Update the `src/main/resources/application.yml` (or `application.properties`) file with your database credentials and preferred storage location:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/filevault
    username: your_db_user
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 55MB

filevault:
  storage:
    location: /var/filevault/uploads # For Windows, use something like C:/filevault/uploads

🏃‍♂️ How to Run
Clone the repository.

Ensure PostgreSQL is running and the filevault database is created.

Build the project using Maven:

Bash
mvn clean install
Run the application:

Bash
mvn spring-boot:run
📡 API Endpoints
Note: All endpoints require the X-User-Id header to simulate an authenticated user's ID.

1. Upload a File
URL: POST /api/files

Headers: X-User-Id: user-123

Body (multipart/form-data):

file: (Binary File - e.g., pdf, png, jpg)

title: "My Document" (Optional)

description: "Important file" (Optional)

category: "Work" (Optional)

2. List User's Files
URL: GET /api/files?page=0&size=10

Headers: X-User-Id: user-123

Response: Paginated JSON list of user's files including metadata and UUIDs.

3. Download a File
URL: GET /api/files/{id}/download

Headers: X-User-Id: user-123

Response: The binary file as an attachment. Returns 403 Forbidden if the X-User-Id does not match the file's owner.

🛡️ Security Highlights
No Path Traversal: File inputs are sanitized using StringUtils.cleanPath and validated against the root directory.

No Malicious Extensions: Executables (.exe, .sh, etc.) are blocked. Only whitelisted extensions are allowed.

UUID Obfuscation: The file's original name is never used on the disk, preventing execution and guessing attacks.

Authorization: Ownership is strictly checked before any file retrieval process.
