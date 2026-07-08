# Laptop Store E-Commerce System

An enterprise-grade, comprehensive Laptop Store application built with modern architecture. 
This project consists of a full-fledged Spring Boot backend and a native Android application.

## 🏗 Architecture Overview

### Backend Architecture
The backend strictly adheres to a **Layered Architecture** pattern:
- **Presentation Layer (Controller)**: Handles HTTP requests/responses, authorization, and routing. No business logic resides here.
- **Application Layer (Facade/Mapper)**: Orchestrates calls between Controllers and Services. Maps Domain Entities to DTOs and vice-versa, keeping Controllers completely decoupled from Entities.
- **Business Layer (Service)**: Contains all the core business logic, validations, exception handling, and transaction management.
- **Data Layer (Repository)**: Interfaces with the MySQL database using Spring Data JPA.

**Tech Stack:** Java 21, Spring Boot 3, Hibernate/JPA, JWT Security, MySQL 8, Maven.

### Android Application
The Android app is built using modern native Android development practices.
- **Architecture**: MVC/MVVM patterns using Navigation Component.
- **Networking**: Retrofit 2 + OkHttp with JWT interceptors.
- **UI & Views**: ViewBinding, Material Design Components, Glide for image loading.
- **Data**: SharedPreferences for session management.

## 🗂 Project Structure

```
d:\LaptopShop\
├── Backend/                 # Spring Boot Backend Project
│   ├── src/main/java/.../
│   │   ├── application/     # Facades, DTOs, Mappers, Security
│   │   ├── business/        # Services, Exceptions
│   │   ├── common/          # Constants, Enums, Utils
│   │   ├── data/            # Entities, Repositories
│   │   └── presentation/    # Controllers
│   └── pom.xml
└── app/                     # Native Android Application
    ├── src/main/java/.../
    │   ├── data/            # API Models, Retrofit Interfaces, Interceptors
    │   └── ui/              # Activities, Fragments, Adapters
    └── build.gradle
```

## 🚀 Key Modules Implemented
1. **Auth & Security**: JWT-based authentication, role-based access control (Admin/User).
2. **User Management**: Profile updates, role management.
3. **Product Catalog**: Categories, Brands, Products, Inventory synchronization.
4. **Cart & Wishlist**: Persistent cart items and user wishlists.
5. **Order Processing**: Checkout workflow, inventory deduction, order state machine.
6. **Payment & Shipment**: Integrated with order workflows.
7. **Coupons & Discounts**: Validation, usage limits.
8. **Reviews**: Product rating system with Admin moderation.
9. **Notifications**: Push notifications with unread counts.
10. **Dashboard**: Admin statistics and reporting.

## 🛠 Setup & Run

### Backend
1. Ensure MySQL is running on `localhost:3306`.
2. Update database credentials in `Backend/src/main/resources/application.properties`.
3. Build and Run the backend (requires JDK 21).

### Android
1. Open the project in Android Studio.
2. The API is configured to connect to `10.0.2.2:8080`.
3. Run the app on an Emulator.

---
*Created as part of a Senior-level architecture demonstration.*
