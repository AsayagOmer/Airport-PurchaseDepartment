# Airport Duty-Free Purchasing System

The Airport Duty-Free Purchasing System is a comprehensive, enterprise-grade software solution designed to handle passenger authentication, dynamic cart management, real-time discount calculations, and seamless checkout operations.

This repository demonstrates a modern approach to application architecture, utilizing JavaFX for the frontend, robust Java service layers, PostgreSQL for data persistence, and a decoupled Python Machine Learning microservice for personalized recommendations.

---

## 🏗️ System Architecture

The project is structured using a multi-layered architecture, cleanly separating concerns to ensure scalability, maintainability, and testability.

### 1. Presentation Layer (JavaFX)
- **Component**: `ui.DashboardApp`
- **Description**: A modern, dark-themed JavaFX desktop application. It acts as the interactive Kiosk for the cashier or passenger.
- **Key Traits**: Responsive UI components, distinct visual panels for authentication, cart management, and personalized recommendations.

### 2. Service & Business Logic Layer
- **Component**: `service.CheckoutService`, `process.CalcDiscount`
- **Description**: The brain of the application. It orchestrates the flow of data between the UI and the database. It handles complex business rules, such as executing multi-tiered dynamic pricing algorithms (e.g., "1+1", "20% off").

### 3. Data Access Layer (DAO Pattern)
- **Component**: `dao.PassengerDAO`, `dao.ProductDAO`, `repository.PurchaseRepository`
- **Description**: Handles all interactions with the PostgreSQL database. By abstracting SQL queries into dedicated Data Access Objects, the business logic remains entirely database-agnostic. 
- **Database Connection**: Managed securely via a Singleton `DatabaseConnectionManager` that loads credentials from a `.properties` file.

### 4. Machine Learning Microservice (Python)
- **Component**: `ml_service/` (FastAPI)
- **Description**: A separate, decoupled Python microservice communicating via REST APIs. It is responsible for advanced predictive analytics, such as generating personalized "Recommended Products" based on passenger demographics and historical purchasing behavior.

---

## ✨ Core Features

* **Instant Passenger Authentication**: Quickly scan and authenticate passengers via their passport numbers securely against the database.
* **Cryptographic Security**: Enforces one-way SHA-256 hashing on all sensitive passenger data (like passports) to prevent plain-text exposure even if the database is compromised.
* **Dynamic Cart Management**: Real-time cart updates with continuous evaluation of active sale rules.
* **Smart ML Recommendations**: Upon authentication, the system suggests personalized products to increase average transaction value.
* **Secure Automated Checkout**: Processes payments, generates comprehensive receipts, and meticulously updates inventory logs to prevent stockouts.

---

## ⚡ Important Technical Facts & Highlights

The system leverages several advanced programming paradigms to maximize performance and reliability.

### 1. High-Performance Parallelization & Concurrency
To ensure the system remains highly responsive under heavy load (e.g., large shopping carts or busy airport peak hours), parallel operations are heavily utilized:
* **Parallel Streams for Calculations**: In `CheckoutService`, calculating the total cost of a cart utilizes Java 8 `parallelStream()`. This allows the application to map and reduce the cost of multiple individual cart items concurrently across multiple CPU cores.
* **JDBC Batch Processing**: In `PurchaseRepository`, finalizing a checkout requires multiple database inserts (purchase items) and updates (stock decrementing). Instead of executing these queries one-by-one sequentially, the system utilizes JDBC `addBatch()` and `executeBatch()`. This executes the entire transaction in a single database round-trip, drastically reducing network latency and improving database throughput.
* **Asynchronous Network I/O**: The Java application requests data from the Python ML Microservice asynchronously on a separate background thread. This ensures the main JavaFX Application Thread is never blocked by network latency, keeping the UI perfectly smooth and responsive.

### 2. Extreme Resilience: Offline Database Fallbacks
To support robust local development and prevent catastrophic failures during network outages, the DAOs implement an automatic "Offline Fallback Mode". 
If the PostgreSQL database is unreachable (e.g., Docker daemon is down or `Connection Refused`), the system seamlessly pivots to **in-memory mock data**. It mocks passenger profiles, product catalogs, and simulates successful checkout transactions, allowing developers and QA to test the UI and business logic 100% locally without spinning up a database container.

### 3. Cryptographic Security Engine
The system employs a strict one-way hashing protocol for all Personally Identifiable Information (PII) used in authentication.
* Before any passport number touches the Data Access Layer or is sent across the network to the ML Microservice, it is passed through Java's native `MessageDigest` to generate an unrecognizable **SHA-256** hash.
* All database queries operate strictly on these hashed strings. This ensures that a compromised database yields absolutely zero plain-text passwords or passport numbers to attackers.

### 4. Behavior-Driven Development (BDD) Pipeline
The core pricing algorithms (which dictate revenue) are protected by a strict Behavior-Driven Development pipeline using **Cucumber**. 
* Business requirements are written in plain English Gherkin syntax (`checkout.feature`), ensuring non-technical stakeholders can verify the logic.
* These feature files automatically map to JUnit assertions (`CheckoutSteps.java`), preventing regressions in discount calculations.

### 5. Modular Build System
The project uses **Maven** as its build tool, cleanly managing dependencies (JavaFX, PostgreSQL Driver, Cucumber) and standardizing the directory structure (`src/main/java`, `src/test/java`).

---

## 🚀 How to Run the System

Because the architecture relies on two separate services, both must be running simultaneously to experience the full feature set.

**1. Start the Machine Learning Microservice (Python)**
```bash
cd ml_service
pip install -r requirements.txt
uvicorn main:app --port 8000
```

**2. Start the Java Desktop Application (JavaFX)**
Open a new terminal at the root of the project:
```bash
mvn clean javafx:run
```

*Note: Once the UI launches, you can test the system by entering passenger passport `12345` to see the ML recommendations dynamically load.*
