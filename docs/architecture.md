# RiggerConnect Android Architecture

## Overview

This document explains the clean architecture implemented in the RiggerConnect Android application. The architecture follows the MVVM pattern using StateFlow, Jetpack Compose for UI, Hilt for dependency injection, Room for the database, and Navigation component for in-app navigation.

## Package Structure

1. **Core Layer**
   - **core-ui**: Shared UI components and themes.
   - **core-common**: Common utilities and extensions.
   - **core-navigation**: Navigation graphs and logic.
   - **core-network**: Network configurations and interceptors.
   - **core-database**: Database setup and configuration using Room.

2. **Domain Layer**
   - **domain**: Contains business logic. Interacts with Use Cases.

3. **Data Layer**
   - **data**: Handles data operations and defines repositories. Includes network and database handling.

4. **Presentation Layer**
   - **app**: The application module containing the UI composed of screens and view models.
   - **feature modules**: Each feature has its module, allowing for modularization and easy scaling.

5. **Feature Modules**
   - **feature-auth**: Authentication related screens and logic.
   - **feature-jobs**: Job-related UI and logic.
   - **feature-profile**: User profile management UI and logic.
   - **feature-payments**: Payments handling UI and logic.
   - **feature-onboarding**: User onboarding UI and related logic.
   - **feature-compliance**: Compliance and auditing screens and business logic.

## Dependency Injection (DI) Graph

Hilt is used for dependency injection, providing a modular approach to inject dependencies by modules.

- **AppModule**: Provides application-wide dependencies.
- **NetworkModule**: Provides network-related dependencies such as Retrofit and OkHttp.

## Database Schema

The application uses Room as a local database solution.

- Entities:
  - **User**: Represents user-related data.
  - **Job**: Represents job-related data.
- DAOs:
  - **UserDao**: Methods to interact with user data.
  - **JobDao**: Methods to interact with job data.

## Module Dependencies

```kotlin
// Example of module dependency for illustration purposes
// Domain Module depends on Data Module
implementation(project(":data"))

// App Module depends on Domain and UI Modules
implementation(project(":domain"))
implementation(project(":core-ui"))
```

This architecture aims to promote scalability, maintainability, and testability across the project while following industry and Android best practices.
