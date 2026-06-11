# Machine Learning Microservice (Python)

This document outlines the design and capabilities of the Python-based Machine Learning microservice that complements the Airport Management System. 

## Overview
The ML Microservice acts as a data consumer and predictive engine. It connects to the shared PostgreSQL database (or an event stream/data lake built from it) to process historical and real-time purchasing data, passenger flow, and product stock levels.

## Features

### 1. Data Engineering & Preprocessing
- **Data Ingestion pipelines**: Connects to the main `dutyfree_db` and extracts raw transaction logs (`Purchasing_table`, `Purchase_Items`) and passenger data.
- **Data Cleansing**: Handles missing values, deduplicates records, and formats timestamps for time-series analysis.
- **Feature Engineering**: 
  - *Time-based features*: Time of day, day of week, seasonality, and proximity to flight departure times.
  - *Passenger profiles*: Aggregated spending habits, preferred product categories, and purchasing frequency.
  - *Product metrics*: Velocity of sales, stock depletion rates, and discount correlation.

### 2. Predictive Models

#### A. Airport Load & Sales Forecasting
Predicts the expected load of passengers in the duty-free areas and estimates sales volume.
- **Model**: Time Series Forecasting (e.g., ARIMA, Prophet, or LSTM).
- **Use Case**: Allows management to optimize staffing at kiosks and cash registers during peak hours.

#### B. Dynamic Inventory Management
Predicts when a product will run out of stock based on current purchasing velocity and seasonal trends.
- **Model**: Regression / Survival Analysis.
- **Use Case**: Automatically alerts the purchasing department to restock high-demand items (e.g., alcohol, perfumes) before they deplete, preventing revenue loss.

#### C. Personalized Product Recommendations
Provides tailored product suggestions to passengers when they scan their boarding pass or passport at the Kiosk.
- **Model**: Collaborative Filtering / Content-based Recommendation System.
- **Use Case**: Increases the average transaction value by suggesting items often bought together or items popular among passengers with similar demographics.

## Technical Stack
- **Framework**: FastAPI or Flask for lightweight, high-performance API endpoints.
- **Data Processing**: Pandas, NumPy, SQLAlchemy.
- **Machine Learning**: Scikit-learn, XGBoost, PyTorch/TensorFlow (for deep learning models).
- **Deployment**: Dockerized and orchestrated alongside the Java application via Docker Compose.

## Integration with Java Application
The Java application (or the Kiosk frontend) communicates with the Python microservice via REST APIs:
- `GET /api/v1/recommendations/{passenger_id}` -> Returns recommended `product_id`s.
- `GET /api/v1/forecast/sales` -> Returns expected sales for the next X hours.
