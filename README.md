# SGI-Guard 🛡️

**SGI-Guard** is a backend service developed as part of my graduation project (degree project). The purpose is to help users protect their **SGI** (*Sjukpenninggrundande inkomst* / Sickness benefit qualifying income) by analyzing and validating work shifts and employment rates.

## 🚀 About the Project
This project focuses on automating calculations for SGI protection according to the Swedish Social Insurance Agency's regulations. It warns users if their worked hours or activity levels risk negatively affecting their benefit levels.

### Key Features
- **Shift Registration:** Log worked hours and employment intensity.
- **SGI Analysis:** Calculate whether current work patterns meet the legal requirements for SGI protection.
- **REST API:** A robust backend built with Spring Boot, ready for frontend integration.

## 🛠 Technologies
- **Java 21**
- **Spring Boot 4.x**
- **Spring Data JPA** (Persistence)
- **PostgreSQL** (Database)
- **H2** (Database for testing)
- **Spring Validation** (Input validation)
- **Lombok** (Boilerplate reduction)

## 🏗 Architecture & Data Model
To ensure SGI protection logic, the system uses a relational model centered around the person and their work-life balance.

```mermaid
erDiagram
    PERSON ||--o{ CHILD : "parent of"
    PERSON ||--o{ WORK_CONDITIONS : "has"
    PERSON ||--o{ SHIFT : "performs"

    PERSON {
        long id PK
        string personalNumber UK
        string firstName
        string lastName
    }

    CHILD {
        long id PK
        string firstName
        date birthDate
        boolean sgiProtecting
        long personId FK
    }

    WORK_CONDITIONS {
        long id PK
        int currentEmploymentRate
        int originalEmploymentRate
        date validFrom
        date validTo
        long personId FK
    }

    SHIFT {
        long id PK
        date startDate
        date endDate
        time startTime
        time endTime
        long personId FK
    }
```

🏁 Getting Started

Prerequisites

* Java 21 SDK
* Maven
* A running PostgreSQL instance


