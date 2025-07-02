# 📦 Funds Transfer API

A Kotlin-based Spring Boot service that enables **currency conversion** and **concurrent-safe money transfers** between accounts, using suspend functions, retry logic, optimistic locking, and external exchange rate APIs.

---

## 🚀 Features

- 🏦 Transfer funds with **exchange rate conversion**
- 🔁 **Retry mechanism** for optimistic lock failures
- 🔐 **Transactional** transfer with isolation
- ⚖️ Uses **BigDecimal** for precision
- 🌍 External API for real-time exchange rates
- ✅ Tested with **unit and concurrency tests**
- 🧪 Built with **WebClient**, **MockK**, and **JUnit 5**

---

## ⚙️ Tech Stack

- Kotlin
- Spring Boot
- Coroutines
- WebFlux WebClient
- Flyway (for DB migrations)
- PostgreSQL
- JPA / Hibernate
- Test: MockK, JUnit 5

---

## 📁 Project Structure

```
src
├── main
│   ├── kotlin
│   │   └── com.financial.funds.transfer
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── config/
│   └── resources
│       └── application.properties
├── test
│   └── kotlin
│       └── com.financial.funds.transfer
│           └── service/
```

---

## 🔑 Environment Configuration

Set environment variables or use `application.properties`:

```properties
# application.properties
exchange.api.base-url=https://api.exchangeratesapi.io/v1/latest
exchange.api.access-key=${EXCHANGE_API_KEY}
```

To avoid committing secrets:
```bash
export EXCHANGE_API_KEY=your_secret_key
```

---

## 🧪 Running Tests

```bash
./gradlew clean test
```

Includes:
- ✅ Unit tests for `TransferService`, `TransferController`
- 🔁 Concurrent test validating balance correctness

---

## ▶️ Running the App

```bash
./gradlew bootRun
```

Example API call:

```http
POST /api/transfer
{
  "fromId": 1,
  "toId": 2,
  "amount": 100.00,
  "fromCurrency": "EUR"
}
```

