# Interactive System Architecture Sketch

Below is an interactive sketch of the Airport Duty-Free Purchasing System. It outlines the flow of data from the User Interface down to the Database and external microservices.

> [!TIP]
> You can interact with the diagram by scrolling to zoom or dragging to pan if your viewer supports it. The colors represent different architectural layers.

## High-Level Architecture Flow

```mermaid
graph TD
    %% User Interface
    User(("🧑‍💼 Cashier / Passenger"))
    UI["💻 JavaFX Dashboard\n(ui.DashboardApp)"]
    
    %% Service Layer
    CS["⚙️ CheckoutService\n(service.CheckoutService)"]
    CD["🧮 CalcDiscount Engine\n(process.CalcDiscount)"]
    MLC["🔌 MLServiceClient\n(service.MLServiceClient)"]
    
    %% DAOs
    PassDAO["📇 PassengerDAO"]
    ProdDAO["📦 ProductDAO"]
    PurchRepo["🛒 PurchaseRepository"]
    
    %% Databases / External
    DB[("🗄️ PostgreSQL\n(dutyfree_db / Mocks)")]
    MLMicro["🧠 Python FastAPI\n(ML Microservice)"]
    
    %% Flow Connections
    User -->|Interacts| UI
    
    UI -->|Authenticates & Cart| CS
    UI -.->|Async Thread| MLC
    
    CS -->|Parallel Stream Calc| CD
    CS -->|Fetch Passenger| PassDAO
    CS -->|Fetch Product| ProdDAO
    CS -->|Execute Transaction| PurchRepo
    
    PassDAO -->|Read| DB
    ProdDAO -->|Read| DB
    PurchRepo -->|Batch Insert/Update| DB
    
    MLC -->|HTTP GET - Port 8000| MLMicro
    
    %% Styling Classes
    classDef ui fill:#4CAF50,stroke:#388E3C,stroke-width:2px,color:white,font-weight:bold;
    classDef service fill:#2196F3,stroke:#1976D2,stroke-width:2px,color:white,font-weight:bold;
    classDef dao fill:#FF9800,stroke:#F57C00,stroke-width:2px,color:white,font-weight:bold;
    classDef db fill:#9C27B0,stroke:#7B1FA2,stroke-width:2px,color:white,font-weight:bold;
    classDef ext fill:#E91E63,stroke:#C2185B,stroke-width:2px,color:white,font-weight:bold;
    classDef user fill:#607D8B,stroke:#455A64,stroke-width:2px,color:white,font-weight:bold;
    
    class UI ui;
    class CS,CD,MLC service;
    class PassDAO,ProdDAO,PurchRepo dao;
    class DB db;
    class MLMicro ext;
    class User user;
```

### Legend
*   **Green**: Presentation Layer (UI)
*   **Blue**: Business Logic & Service Layer
*   **Orange**: Data Access Layer (DAOs)
*   **Purple**: Core Persistence (Database)
*   **Pink**: External Microservices (Machine Learning)
