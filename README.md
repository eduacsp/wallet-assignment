# Wallet Assignment API
RESTful API for managing digital wallets with support for deposits, withdrawals, transfers, and balance queries (current and historical). Built with Spring Boot 3, Java 21, and tested using JUnit 5 with code coverage via JaCoCo.

## Features
- Create wallet with initial deposit

- Check current balance

- Check historical balance by date/time

- Deposit and withdraw funds

- Transfer between wallets

## Installation & Execution
### Prerequisites

- JDK 21

- Gradle (wrapper included)

- IDE

### Clone the repository
```bash
git clone https://github.com/eduacsp/wallet-assignment.git
cd wallet-assignment
```

### Build and run locally
```bash
./gradlew bootRun
```
The application will run at: http://localhost:8081

### Run tests + generate coverage report
```bash
./gradlew test jacocoTestReport
```
Coverage report will be available at:

```bash
build/reports/jacoco/test/html/index.html
```
## API Endpoint Examples
### Create wallet

```bash
curl --location 'localhost:8081/wallets' \
--header 'Content-Type: application/json' \
--data '{
    "user": {"name":"Eduardo","cpfCnpj":"25786399818"},
    "value": 20000.0
}'
```

### Deposit

```bash
curl --location 'localhost:8081/users/25786399818/wallet/deposit' \
--header 'Content-Type: application/json' \
--data '{
  "amount": 100.00,
  "description": "bonus"
}'
```

### Check balance

```bash
curl --location 'localhost:8081/users/25786399818/wallet/balance'
```
### Historical balance

```bash
curl --location 'localhost:8081/users/25786399818/wallet/balance/at?datetime=2025-04-08T15%3A00%3A00'
```

### Transfer

```bash
curl --location 'localhost:8081/wallets/transfer' \
--header 'Content-Type: application/json' \
--data '{
  "fromCpfCnpj": "25786399818",
  "toCpfCnpj": "25786399819",
  "amount": 50.00,
  "description": "Pagamento de dívida"
}
'
```
### Withdraw

```bash
curl --location 'localhost:8081/users/25786399818/wallet/withdraw' \
--header 'Content-Type: application/json' \
--data '{
  "amount": 100.00,
  "description": "special needs"
}'
```

## Design Decisions
### Strategy via Strategy Pattern
The logic for deposit, withdrawal, and transfer was separated using the Strategy Pattern, ensuring extensibility for future transaction types and adhering to the Open/Closed Principle (OCP).

### Traceability and Auditability
Each transaction is recorded as a persistent entity with enough data to rebuild the wallet's history. This satisfies non-functional requirements such as traceability and auditing.

### Responsibility Segregation
- WalletServiceImpl is responsible for orchestration and business validation.

- Strategies encapsulate the specific rules for each transaction type.

- Balance calculation is delegated to BalanceService, allowing flexible changes to the calculation logic.

- A retry mechanism (3 attempts) is applied to each database interaction to improve resilience.

## Trade-offs and Limitations
- Simplified Persistence
  To ease testing and reduce complexity, the project uses H2 in-memory database. In production, PostgreSQL or another ACID-compliant relational DB is recommended.

- No Authentication/Authorization
  Due to time constraints, no security/authentication layer was implemented. The API accepts any CPF/CNPJ as a valid user identifier. For real-world scenarios, JWT authentication and RBAC should be added.

- Simple Error Handling
  Error handling is centralized with friendly messages, but there’s no global exception mapping using @ControllerAdvice for simplicity. This can be easily added to standardize error responses.


