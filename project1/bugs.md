# 🐛 Intentional Bugs & Interview Guide

This document details the intentional bugs hidden in the project, how they manifest, and how a candidate is expected to debug and fix them.

---

## 🛑 Bug #1: Runtime API Failure (Create Order)

### The Issue
The application starts successfully, but the **Create Order** API (`POST /api/orders`) fails with a `500 Internal Server Error`.

### Technical Detail
*   **Location:** `com.retail.order.entity.Order.java` and `com.retail.order.service.OrderService.java`
*   **Root Cause:**
    *   The `Order` entity has a field `createdAt` marked as `@Column(nullable = false)`.
    *   The `OrderService.createOrder` method instantiates a new `Order` object but **never sets** the `createdAt` value.
    *   When JPA tries to persist (save) the entity, the database (or Hibernate validation) throws an exception because the non-nullable column is null.

### Expected Debugging Approach
1.  **Reproduce:** Candidate runs the application and executes the `curl` command for creating an order. They see a 500 error.
2.  **Logs:** They should check the application logs. They will see a stack trace involving `DataIntegrityViolationException` or `ConstraintViolationException` referencing `NULL not allowed for column "CREATED_AT"`.
3.  **Code Analysis:** They should look at `OrderService.java` and notice `createdAt` is missing from the setter calls.

### The Fix
Update `OrderService.java` to set the timestamp:

```java
// com.retail.order.service.OrderService.java

public OrderResponse createOrder(CreateOrderRequest request) {
    // ... existing code ...
    order.setAmount(request.getAmount());
    order.setStatus(request.getStatus());
    order.setCreatedAt(LocalDateTime.now()); // <--- ADD THIS LINE
    
    Order savedOrder = orderRepository.save(order);
    // ...
}
```
*Alternative Fix:* Add `@CreationTimestamp` (Hibernate annotation) or `@EntityListeners(AuditingEntityListener.class)` + `@CreatedDate` to the entity field.

---

## 🛑 Bug #2: Intermittent Read Failure (Get Order)

### The Issue
Fetching an **existing** order works fine (`200 OK`), but fetching a **non-existent** order results in a `500 Internal Server Error` instead of a `404 Not Found`.

### Technical Detail
*   **Location:** `com.retail.order.service.OrderService.java`
*   **Root Cause:**
    *   The repository method `findByOrderNumber` returns an `Optional<Order>`.
    *   The service code calls `.get()` directly on the Optional without checking if a value is present: `orderRepository.findByOrderNumber(orderNumber).get()`.
    *   When the order is missing, `.get()` throws a `java.util.NoSuchElementException`. Since this is an unchecked exception dealing with logic, it bubbles up as a 500 error (unless specifically handled).

### Expected Debugging Approach
1.  **Reproduce:** call `GET /api/orders/INVALID-ID`.
2.  **Observation:** Receive 500 status code. Expected behavior for a REST API is 404 for missing resources.
3.  **Code Analysis:** Candidate inspects `OrderService.java` and identifies the unsafe usage of `Optional.get()`.

### The Fix
Refactor the service to handle the empty case and ideally throw a custom exception mapped to 404.

```java
// com.retail.order.service.OrderService.java

public OrderResponse getOrderByNumber(String orderNumber) {
    Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")); // <--- FIX
            
    return mapToResponse(order);
}
```

---

## 🛑 Bug #3: Docker Restart Loop (Critical)

### The Issue
The application runs fine locally (via `java -jar`), but when run in Docker, the container starts and then keeps restarting or becomes "Unhealthy" indefinitely.

### Technical Detail
There are **two** issues contributing to this, testing Docker knowledge:

**1. Health Check Path Mismatch (Configuration)**
*   **Location:** `src/main/resources/application.yml` and `Dockerfile`
*   **Root Cause:**
    *   `application.yml` changes the actuator base path:
        ```yaml
        management:
          endpoints:
            web:
              base-path: /internal  <--- MOVED HERE
        ```
    *   `Dockerfile` tries to healthcheck the default path:
        ```dockerfile
        HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health || exit 1
        ```
    *   The curl command receives a 404, causing the container to report as `unhealthy`, which might trigger orchestrators to restart it (or just fail the simple docker run status).

**2. Memory Misconfiguration (JVM)**
*   **Location:** `Dockerfile`
*   **Root Cause:**
    *   The Dockerfile sets `ENV JAVA_TOOL_OPTIONS="-Xmx16m"`.
    *   16MB heap is definitely too small for a modern Spring Boot 3 application to initialize fully.
    *   The container will crash with code 1 or 137 (OOM Killer) repeatedly during startup.

### Expected Debugging Approach
1.  **Observe:** Run `docker run ...` and see it exiting immediately.
2.  **Inspect:** Run `docker ps -a` to see exit codes.
3.  **Logs:** Run `docker logs <container_id>`.
    *   They will see `java.lang.OutOfMemoryError` or simply the JVM crashing.
    *   Or, if it stays up but says "unhealthy", they should exec into the container (`docker exec -it <id> sh`) and check `curl` or hit the healthcheck. Note that `curl` might not be installed by default in some images, but here we added `apk add curl`.

### The Fix
**Step 1: Fix Memory**
Increase heap size in `Dockerfile` (or remove the limit to let container defaults work):
```dockerfile
ENV JAVA_TOOL_OPTIONS="-Xmx512m" 
```

**Step 2: Fix Healthcheck**
Update the URL in `Dockerfile` to match the configuration:
```dockerfile
HEALTHCHECK ... CMD curl -f http://localhost:8080/internal/health || exit 1
```
