# QA Test Result Service

A **Spring Boot 3 (Java 21)** microservice for storing and querying automated test execution results (e.g., from TestNG-based frameworks).
Results are persisted in **MongoDB** and exposed via a **JSON API**, with a small **Java client library** for convenient consumption.

---

## 1. Features

* **Test result persistence** in MongoDB (`testMethodResults` collection)
* **Filter & query APIs**

  * By **runId** (with optional **status** filter)
  * By **suiteType** (with optional **status** filter)
  * By **featureName** (with optional **status** filter)
* **Paginated runId API** to fetch distinct runIds per `iata`
* **DTO-based controllers** to isolate persistence entities
* **Global exception handling** using RFC 7807 `ProblemDetail`
* **Log4j2 logging** (via Lombok `@Log4j2`)
* **MongoDB indexes** tuned for common queries
* **Client library** (`client-lib`) for convenient integration from test frameworks

---

## 2. Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.5.x (Spring Web, Spring Data MongoDB)
* **Database**: MongoDB
* **Build**: Maven
* **Logging**: Log4j2 (`spring-boot-starter-log4j2`, `log4j2.properties`)
* **Docs**: springdoc-openapi (Swagger UI)

---

## 3. Project Structure (High Level)

```
src/main/java/aero/airfi/qa
├── QaTestResultServiceApplication.java     # Spring Boot entry point
├── model                                  # MongoDB entities
│   ├── TestMethodResult
│   ├── EnvironmentInfo
│   └── FailureDetail
├── dto                                    # API DTOs
│   ├── TestMethodResultRequest
│   ├── TestMethodResultResponse
│   ├── EnvironmentInfoDto
│   ├── FailureDetailDto
│   ├── ApiResponse
│   └── ErrorResponse
├── controller
│   ├── TestMethodResultController         # REST endpoints
│   └── ControllerMapper                   # DTO ↔ Entity mapper
├── service
│   └── TestMethodResultService            # Business logic & aggregations
├── repository
│   └── TestMethodResultRepository         # Mongo repository
├── config
│   ├── MongoConfig
│   └── OpenApiConfig
├── exception
│   ├── GlobalExceptionHandler
│   └── ResourceNotFoundException
└── resources
    ├── application.properties
    └── log4j2.properties

client-lib/                                # Java client library
```

---

## 4. Running the Service

### 4.1 Prerequisites

* Java 21

  ```bash
  java -version
  ```

* Maven 3.9+

* MongoDB running locally:

  * **host**: `localhost`
  * **port**: `27017`
  * **database**: `test_results`

### Mongo Configuration (`application.properties`)

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=test_results
spring.data.mongodb.auto-index-creation=true
```

---

### 4.2 Build

```bash
mvn clean package
```

---

### 4.3 Run

```bash
mvn spring-boot:run
# OR
java -jar target/qa-test-result-service-0.0.1-SNAPSHOT.jar
```

Default port: **8080**
Override using:

```properties
server.port=8081
```

---

## 5. API Overview

### Base Path

```
/api/{iata}/test-results
```

### Common API Response Wrapper

All successful endpoints return:

```json
{
  "success": true,
  "data": "...",
  "message": "OK",
  "timestamp": "2025-01-01T00:00:00Z",
  "path": "/api/XX/test-results/..."
}
```

Errors use **RFC 7807 `ProblemDetail`** via `GlobalExceptionHandler`.

---

### 5.1 Create a Test Result

**Endpoint**

```http
POST /api/{iata}/test-results
Content-Type: application/json
```

**Request Body (`TestMethodResultRequest`)**

```json
{
  "runId": "run-123",
  "suiteType": "PAX1",
  "featureName": "Login",
  "methodName": "testLogin",
  "className": "com.example.tests.LoginTest",
  "status": "PASS",
  "assertType": "HARD",
  "startTime": "2025-01-01T10:00:00Z",
  "endTime": "2025-01-01T10:00:01Z",
  "durationMs": 1000,
  "jiraTestCases": ["TC-01"],
  "environment": {
    "name": "QA",
    "browser": "Chrome",
    "browserVersion": "121",
    "os": "Windows",
    "buildNumber": "1.0.0"
  },
  "logs": ["step1", "step2"],
  "screenshotUrls": ["https://.../screenshot.png"],
  "failures": []
}
```

---

### 5.2 Get Results by `runId` (Optional Status)

```http
GET /api/{iata}/test-results/{runId}?status=PASS|FAIL|SKIP
```

* `status` is **optional**
* Valid values: `PASS`, `FAIL`, `SKIP` (case-insensitive)
* Invalid values → **400 Bad Request**

---

### 5.3 Get Results by `suiteType` (Optional Status)

```http
GET /api/{iata}/test-results/suite/{suiteType}?runId={runId}&status=PASS|FAIL|SKIP
```

Filters by:

* `runId`
* `suiteType`
* Optional `status`

---

### 5.4 Get Results by `featureName` (Optional Status)

```http
GET /api/{iata}/test-results/feature/{featureName}?runId={runId}&status=PASS|FAIL|SKIP
```

Filters by:

* `runId`
* `featureName`
* Optional `status`

---

### 5.5 Paginated `runId` List

```http
GET /api/{iata}/test-results/run-ids?page={page}
```

* Returns **distinct** `runId`s per `iata`
* `page` is **0-based**
* Page size: **10**

**Example Response Data**

```json
{
  "page": 0,
  "size": 10,
  "runIds": ["run-3", "run-2", "run-1"]
}
```

---

## 6. Error Handling

Common cases:

* **404 Not Found** – Data not found for filters
* **405 Method Not Allowed** – Invalid HTTP method
* **400 Bad Request** – Invalid query parameters

### Example: Invalid `status`

```json
{
  "type": "about:blank",
  "title": "Invalid Request Parameter",
  "status": 400,
  "detail": "Invalid value for query parameter 'status': 'FOO'. Allowed values are PASS, FAIL, SKIP. Use ?status=PASS|FAIL|SKIP",
  "timestamp": "2025-01-01T10:00:00Z",
  "path": "/api/XX/test-results/123"
}
```

---

## 7. MongoDB Indexing

Auto-created at startup:

```properties
spring.data.mongodb.auto-index-creation=true
```

### Key Indexes

* `@Indexed` → `runId`
* `@Indexed` → `iata`
* `@Indexed` → `status`
* `@Indexed` → `startTime`
* `@TextIndexed` → `featureName`

Optional compound index example:

```json
{ "runId": 1, "suiteType": 1, "status": 1, "iata": 1 }
```

---

## 8. Logging

* **Log4j2** via `log4j2.properties`
* Lombok `@Log4j2` used in:

  * `TestMethodResultController`
  * `TestMethodResultService`
  * `GlobalExceptionHandler`

---

## 9. Client Library

Located in:

```
client-lib/
```

### Sample Usage

```java
QaTestResultServiceClient client =
        new QaTestResultServiceClient("http://localhost:8080");

// Create result
String id = client.createTestResult(testMethodResultDto);

// Query results
List<TestMethodResult> results = client.getByRunId("run-123");
```

> Extend methods to include `iata` in the request path if needed.

---

## 10. Extending the Service

Planned enhancements:

* Batch ingestion for bulk uploads
* Advanced filters (time range, `assertType`, environment)
* Metrics APIs (pass rate by run / feature / suite)
* Authentication & authorization for multi-tenant access
