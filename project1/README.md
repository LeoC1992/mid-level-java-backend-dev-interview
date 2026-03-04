# Retail Order Management Service

A Spring Boot microservice for managing retail orders, built with Java 17 and Spring Data JPA.

## Prerequisites

- Java 17
- Maven 3.6+
- Docker (for containerization)

## Project Structure

- `src/main/java`: Source code
- `src/main/resources`: Configuration and data
- `Dockerfile`: Container image definition

## Build

To compile and package the application:

```bash
mvn clean package
```

## Run Locally

To run the application directly:

```bash
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

The server will start on port `8080`.

### Database
This project uses H2 in-memory database.
- Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: `password`

## Docker

### Build Image

```bash
docker build -t retail-order-service .
```

### Run Container

```bash
docker run -p 8080:8080 --restart always retail-order-service
```

## API Reference

### 1. Create Order
**POST** `/api/orders`

```bash
curl -v -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD-9999",
    "customerName": "Demo User",
    "amount": 99.99,
    "status": "CREATED"
  }'
```

### 2. Get Order
**GET** `/api/orders/{orderNumber}`

```bash
curl -v http://localhost:8080/api/orders/ORD-9999
```

### 3. List Orders
**GET** `/api/orders`

```bash
curl -v "http://localhost:8080/api/orders?page=0&size=10"
```

### 4. Health Check
**GET** `/actuator/health`

```bash
curl -v http://localhost:8080/actuator/health
```
