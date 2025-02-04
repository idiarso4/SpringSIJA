# Face Recognition Attendance System

A comprehensive attendance management system with face recognition capabilities and PKL (Praktik Kerja Lapangan/Internship) management features.

## Features

- **Face Recognition**
  - Face registration and verification
  - Liveness detection
  - High accuracy matching

- **Attendance Management**
  - Automated check-in/check-out
  - Location validation
  - Real-time attendance tracking
  - Comprehensive reporting

- **PKL Management**
  - Student assignments
  - Daily activity tracking
  - Supervisor approvals
  - Company management

- **Security**
  - JWT-based authentication
  - Role-based access control
  - Secure password management
  - API security

## Technology Stack

- **Backend**
  - Java 17
  - Spring Boot 3.x
  - Spring Security
  - Spring Data JPA
  - PostgreSQL
  - OpenCV
  - JWT

- **Development Tools**
  - Maven
  - Flyway (Database Migration)
  - JUnit 5
  - Mockito
  - Swagger/OpenAPI

## Prerequisites

- JDK 17 or later
- PostgreSQL 14 or later
- Maven 3.8 or later
- OpenCV 4.x

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/attendance-system.git
   cd attendance-system
   ```

2. **Configure environment variables**
   Create a `.env` file in the project root:
   ```properties
   # Database
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/attendance_db
   SPRING_DATASOURCE_USERNAME=your_username
   SPRING_DATASOURCE_PASSWORD=your_password

   # JWT
   JWT_SECRET=your_jwt_secret_key
   
   # Email
   MAIL_USERNAME=your_email@gmail.com
   MAIL_PASSWORD=your_email_password
   
   # File Storage
   FILE_STORAGE_PATH=/path/to/storage
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run database migrations**
   ```bash
   mvn flyway:migrate
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

## Testing

Run the tests using:
```bash
mvn test
```

For integration tests:
```bash
mvn verify
```

## Project Structure

```
attendance-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/attendance/
│   │   │       ├── attendance/    # Attendance management
│   │   │       ├── recognition/   # Face recognition
│   │   │       ├── pkl/          # PKL management
│   │   │       ├── security/     # Security & auth
│   │   │       ├── user/         # User management
│   │   │       └── config/       # Configurations
│   │   └── resources/
│   │       ├── db/migration/     # Flyway migrations
│   │       ├── templates/        # Email templates
│   │       └── application.yml   # Application config
│   └── test/
│       └── java/                 # Test classes
└── pom.xml
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please contact support@attendance-system.com or open an issue in the GitHub repository.
