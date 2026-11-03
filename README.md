# RentHub AI – Apartment Management System Backend

## 1. Introduction

RentHub AI is a backend system for managing apartment buildings, apartments, tenants, rental contracts, invoices, and payments. The system integrates AI chatbot functionality to support users in retrieving apartment-related information.

This project is developed using Java Spring Boot and MySQL, providing RESTful APIs for the frontend application.

## 2. Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- RESTful API
- Google Gemini API
- Maven
- Git & GitHub

## 3. Main Features

- User authentication and authorization.
- Apartment and building management.
- Rental contract management.
- Invoice and payment management.
- User and administrator role management.
- AI chatbot integration using Google Gemini API.

## 4. Security Project

This repository is also used for the course project **"Programming Vulnerability Exploitation"**.

The security assessment focuses on:

- Authorization testing between Admin and User roles.
- Access control verification for apartments, contracts, and invoices.
- SQL Injection testing.
- Input validation and secure database query handling.
- Security vulnerability remediation and verification.

Security-related changes are developed in the `security-testing` branch.

## 5. Installation and Setup

### Prerequisites

- JDK 21
- Maven
- MySQL
- Git

### Clone Repository

```bash
git clone https://github.com/VoThanhTruc24/quan-ly-can-ho-backend.git
cd quan-ly-can-ho-backend
```

### Configure Environment Variables

Create a `.env` file or configure environment variables for database connection and Gemini API.

Example:

```env
DB_URL=jdbc:mysql://localhost:3306/renthub_ai
DB_USERNAME=your_username
DB_PASSWORD=your_password
GEMINI_API_KEY=your_api_key
```

Do not commit `.env` or expose sensitive credentials.

### Run Application

```bash
mvn clean install
mvn spring-boot:run
```

## 6. Development Branches

- `main`: Original application source code.
- `security-testing`: Security assessment, testing, and remediation.

## 7. Project Repository

Backend: https://github.com/VoThanhTruc24/quan-ly-can-ho-backend

Frontend: https://github.com/VoThanhTruc24/quan-ly-can-ho-frontend

