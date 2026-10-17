# SimUPI — UPI Payment Simulation Web Application

> **Academic Project for Second-Year Computer Science / Information Technology Engineering**  
> *A full-stack, simulated Unified Payments Interface (UPI) web application inspired by Google Pay and PhonePe. Designed to demonstrate Core Object-Oriented Programming (OOP) principles, Design Patterns, and Enterprise Spring Boot architecture.*  
> **⚠️ Simulation Only:** No real bank or NPCI integration is used.

---

## 1. Project Overview

SimUPI simulates the real-world architecture of the Unified Payments Interface (UPI):
* **User Lifecycle:** Register, link a virtual bank account (Savings or Current), auto-generate a unique UPI ID (e.g. `anukul@simupi`), and set a 4 or 6-digit UPI PIN.
* **Home Dashboard:** Personalized greeting, virtual ATM/debit card with masked account numbers, live balance preview, and quick action tiles.
* **Atomic P2P Transfers:** Instant push payments with recipient pre-verification, `@Transactional` ACID compliance (no half-completed transfers), unique 12-digit UTR reference generation, and an on-screen numeric PIN keypad.
* **PIN Security & Lockout:** BCrypt hashing for credentials; 3-attempt PIN lockout protection.
* **Passbook & Digital Receipts:** Multi-criteria passbook filtering using Java Stream APIs (by direction, status, date, amount, search query) and printable digital receipts (`window.print()`).
* **Bonus Features:** Dynamic personal UPI QR code generation (`upi://pay?pa=...`), collect request workflow, and central switch admin console with network KPI metrics.

---

## 2. Technology Stack

* **Backend:** Java 17, Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Hibernate Validator, Spring Security Crypto).
* **Database:** Embedded H2 Database in file mode (`./data/simupidb`), persisting across restarts. MySQL profile provided (`application-mysql.properties`).
* **Frontend:** Mobile-first responsive HTML5, CSS3, Vanilla JavaScript (Fetch API).
* **Testing:** JUnit 5 and Mockito service-layer unit tests.
* **API Testing:** Ready-to-import Postman Collection (`SimUPI.postman_collection.json`).

---

## 3. OOP Implementation Matrix

Every OOP requirement is explicitly marked in the code with inline comments (e.g., `// OOP: Polymorphism`):

| OOP Concept | Class / Code Location | Architectural Purpose |
| :--- | :--- | :--- |
| **Encapsulation** | [`User.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/model/User.java), [`BankAccount.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/model/BankAccount.java), [`Transaction.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/model/Transaction.java) | All fields are strictly `private`. State mutations occur via getters, setters, and controlled domain methods like `debit()`, `credit()`, and `recordFailedPinAttempt()`. |
| **Abstraction** | `abstract class BankAccount`, `abstract class Transaction` | Core transaction services interact with abstract contracts without coupling to concrete account types. |
| **Inheritance** | `SavingsAccount`, `CurrentAccount` extend `BankAccount`; `SendMoneyTransaction`, `CollectRequestTransaction` extend `Transaction` | Subclasses reuse common attributes (`accountNumber`, `balance`, `utr`, `amount`) while adding specialized behaviors. |
| **Runtime Polymorphism** | `validateTransfer()`, `calculateCharges()`, `debit()` | Overridden dynamically. Savings accounts enforce ₹1,000 min balance & ₹1,00,000 daily limit; Current accounts enforce ₹5,000 min balance & ₹2,00,000 daily limit. |
| **Compile-Time Polymorphism** | [`PaymentService.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/service/PaymentService.java) | Method overloading for recipient search: `searchUser(String upiId)` vs `searchUser(Long mobile)`. |
| **Interfaces** | `PaymentService`, `NotificationService`, `TransactionRepository`, `BankAccountService` | Decouples high-level business policy from low-level database operations and notifications. |
| **Design Pattern: Factory** | [`BankAccountFactory.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/pattern/factory/BankAccountFactory.java) | Centralizes instantiation of `SavingsAccount` vs `CurrentAccount` with generated 16-digit account numbers and IFSC codes. |
| **Design Pattern: Strategy** | [`FeeCalculationStrategy.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/pattern/strategy/FeeCalculationStrategy.java) | Interchangeable fee algorithms: `SavingsFeeStrategy` (₹0 fee) vs `CurrentFeeStrategy` (0.1% fee on business transfers). |
| **Collections & Streams** | [`TransactionServiceImpl.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/service/impl/TransactionServiceImpl.java) | Fluent Java Stream pipelines (`filter`, `sorted`, `map`, `reduce`) for passbook querying, full-text search, and admin KPI calculations. |
| **Custom Exceptions** | `com.simupi.exception.*` | Domain-specific error handling: `InsufficientBalanceException`, `InvalidPinException`, `DailyLimitExceededException`, `SelfTransferException`, `AccountLockedException`. |

---

## 4. Preloaded Demo Test Accounts

The system automatically initializes 5 demo users with balances and 15 transactions on startup:

| User ID | Full Name | Mobile | Email | UPI ID | Bank Name & Account | Type | Balance | Password | UPI PIN |
| :---: | :--- | :---: | :--- | :--- | :--- | :---: | :---: | :---: | :---: |
| **1** | Anukul Sharma | `9876543210` | `anukul@simupi.com` | `anukul@simupi` | HDFC Bank (`...0001`) | SAVINGS | **₹45,000.00** | `password123` | `1234` |
| **2** | Priya Patel | `9876543211` | `priya@simupi.com` | `priya@simupi` | SBI (`...0002`) | SAVINGS | **₹28,500.00** | `password123` | `1234` |
| **3** | Rahul Verma | `9876543212` | `rahul@simupi.com` | `rahul@simupi` | ICICI Bank (`...0003`) | CURRENT | **₹1,12,000.00** | `password123` | `1234` |
| **4** | Sneha Reddy | `9876543213` | `sneha@simupi.com` | `sneha@simupi` | Axis Bank (`...0004`) | SAVINGS | **₹15,400.00** | `password123` | `1234` |
| **5** | Vikram Singh | `9876543214` | `vikram@simupi.com` | `vikram@simupi` | PNB (`...0005`) | CURRENT | **₹73,500.00** | `password123` | `1234` |

*Tip: On the login page, you can click any of the preloaded demo user chips to instantly log in.*

---

## 5. How to Run the Application

### Prerequisites
* **Java Development Kit (JDK) 17** or higher installed (`java -version`).
* **Apache Maven** installed (`mvn -version`) or an IDE with built-in Maven support.

### Option A: Running from Terminal / Command Prompt
```bash
# Navigate to the project directory
cd "c:\Users\Anukul\Desktop\Java Project"

# Run Spring Boot application
mvn spring-boot:run
```
*(Or double-click `run.bat` on Windows)*

### Option B: Running from Eclipse / IntelliJ IDEA / VS Code
1. Open your IDE and select **Open Folder / Import Project**.
2. Choose `c:\Users\Anukul\Desktop\Java Project` as an existing **Maven Project**.
3. Locate `src/main/java/com/simupi/SimUpiApplication.java`.
4. Right-click $\to$ **Run 'SimUpiApplication.main()'**.

### Accessing the Web Application
* **Web UI Dashboard:** [http://localhost:8080](http://localhost:8080)
* **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  * **JDBC URL:** `jdbc:h2:file:./data/simupidb`
  * **User Name:** `sa`
  * **Password:** *(leave blank)*

---

## 6. Running Unit Tests

Execute the JUnit 5 test suite covering atomic transfers, PIN lockout, method overloading, factory account creation, and fee strategies:

```bash
mvn test
```

Test Results Breakdown:
* `testSendMoney_Success` — Verifies atomic debit & credit and UTR generation.
* `testSendMoney_InsufficientBalance` — Verifies minimum balance enforcement.
* `testSendMoney_SelfTransfer` — Rejects sender == receiver transfers.
* `testSendMoney_DailyLimitExceeded` — Enforces ₹1,00,000 / ₹2,00,000 daily caps.
* `testSendMoney_WrongPinIncrementsFailureCount` — Tracks remaining PIN attempts.
* `testSendMoney_LocksAfterThreeWrongAttempts` — Locks account permanently on 3rd failure.
* `testSendMoney_CorrectPinResetsCounter` — Clears failed attempt counter on success.
* `testSearchUser_MethodOverloading` — Validates compile-time polymorphic search overloads.
* `testBankAccountFactory_Polymorphism` — Validates Factory Pattern instantiations.
* `testFeeCalculationStrategies` — Validates Strategy Pattern calculations.

---

## 7. Viva Q&A Guide (15 Likely Questions & Model Answers)

### Q1: What are the four pillars of OOP and how are they used in this project?
> **Answer:**
> 1. **Encapsulation:** All model attributes (`balance`, `upiPinHash`) are `private`. Access and mutations are governed through getters, setters, and domain methods (`debit()`, `credit()`, `recordFailedPinAttempt()`).
> 2. **Abstraction:** High-level payment logic depends on abstract classes `BankAccount` and `Transaction`.
> 3. **Inheritance:** `SavingsAccount` and `CurrentAccount` inherit common banking attributes from `BankAccount`.
> 4. **Polymorphism:** `validateTransfer()` and `calculateCharges()` are overridden polymorphically to enforce distinct account limits and charges at runtime.

### Q2: Why did you use an Abstract Class for `BankAccount` instead of a regular class or interface?
> **Answer:** An abstract class was chosen because all bank accounts share common state fields (`accountNumber`, `bankName`, `ifscCode`, `balance`) and concrete behaviors (`credit()`, `getMaskedAccountNumber()`), while requiring child classes to enforce specialized rules (`validateTransfer()`, `debit()`). Interfaces in Java cannot hold instance state fields.

### Q3: What is the difference between Method Overriding and Method Overloading in your code?
> **Answer:** 
> * **Method Overloading (Compile-Time Polymorphism):** In `PaymentService`, `searchUser(String upiId)` and `searchUser(Long mobile)` share the same name with different parameter signatures. The compiler determines which method to call at compile-time.
> * **Method Overriding (Runtime Polymorphism):** In `SavingsAccount` and `CurrentAccount`, both override `validateTransfer(amount, dailyDebited)` defined in `BankAccount`. At runtime, the JVM uses dynamic method dispatch to call the child implementation based on actual object type.

### Q4: How is Encapsulation maintained for sensitive balance data?
> **Answer:** In `BankAccount.java`, `balance` is `private`. There is no public `setBalance()`. Balance deductions can only happen through the `debit()` method, which first validates liquidity and minimum balance rules, preventing arbitrary external tampering.

### Q5: How did you implement the Factory Design Pattern?
> **Answer:** In [`BankAccountFactory.java`](file:///c:/Users/Anukul/Desktop/Java%20Project/src/main/java/com/simupi/pattern/factory/BankAccountFactory.java), `createAccount(AccountType, User, bankName, balance)` centralizes the logic for generating 16-digit account numbers and bank IFSC codes, returning either a `SavingsAccount` or `CurrentAccount`. This decouples the service layer from concrete constructor logic.

### Q6: How does the Strategy Design Pattern work in your application?
> **Answer:** We created a `FeeCalculationStrategy` interface with concrete strategies `SavingsFeeStrategy` (₹0 fee) and `CurrentFeeStrategy` (0.1% surcharge for transfers > ₹50,000). The payment engine dynamically selects the strategy based on account type, allowing future fee policies (e.g. merchant or international fees) to be added without touching the payment processor (Open/Closed Principle).

### Q7: Why is `@Transactional` critical for the `sendMoney()` method?
> **Answer:** Money transfers require a debit from the sender and a credit to the receiver. `@Transactional` ensures atomicity (ACID). If the credit fails or the server crashes mid-transfer, Spring automatically rolls back the debit, guaranteeing the system is never left in a half-completed state.

### Q8: How does your system prevent brute-force attacks on UPI PINs?
> **Answer:** The `User` entity tracks `failedPinAttempts`. Each wrong PIN attempt increments the counter. On 3 consecutive wrong attempts, `isPinLocked` is set to `true`, and all subsequent financial transactions are blocked until the user unlocks their PIN using account password verification. Correct PIN entry resets the counter to 0.

### Q9: How are passwords and UPI PINs stored in the database?
> **Answer:** They are never stored in plain text. They are hashed using **BCrypt** (`BCryptPasswordEncoder`) with a work factor of 10. BCrypt incorporates a random salt to protect against dictionary and rainbow table attacks.

### Q10: How does JPA map your OOP inheritance structure to the database?
> **Answer:** We used **Single Table Inheritance** (`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`). Both `SavingsAccount` and `CurrentAccount` reside in the `bank_accounts` table, differentiated by the `@DiscriminatorColumn(name = "account_type")`. This avoids expensive SQL table joins while supporting polymorphic JPA queries.

### Q11: Explain how the Java Stream API was used for passbook filtering.
> **Answer:** In `TransactionServiceImpl.java`, transactions are processed through a fluent stream pipeline:
> ```java
> allUserTx.stream()
>     .filter(...) // Type, status, date, amount, search filters
>     .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed()) // Newest first
>     .map(this::toResponse)
>     .collect(Collectors.toList());
> ```
> This separates data retrieval from presentation filtering cleanly.

### Q12: Why did you create custom exceptions instead of using standard exceptions like `RuntimeException`?
> **Answer:** Custom exceptions like `InsufficientBalanceException` and `DailyLimitExceededException` allow precise, typed error classification. They carry structured metadata (such as `attemptsRemaining` or `currentBalance`), which our `GlobalExceptionHandler` converts into user-friendly HTTP error codes and descriptive messages.

### Q13: How does the Global Exception Handler (`@RestControllerAdvice`) work?
> **Answer:** It intercepts unhandled exceptions thrown anywhere in the controller layer using `@ExceptionHandler`. Instead of leaking stack traces or generic 500 errors to the client, it formats a standardized `ErrorResponse` JSON with appropriate HTTP status codes (e.g., 400 for balance issues, 401 for wrong PIN, 403 for locked accounts).

### Q14: How does receiver pre-verification work in your user flow?
> **Answer:** When the user types a mobile number or UPI ID on the Send Money page, a debounced JavaScript call queries `GET /api/payments/search?query=...`. The backend resolves the user and returns their real name. The frontend displays a green "Verified" badge, giving the sender visual confirmation before they input an amount and PIN.

### Q15: What is the 12-digit UTR and how is it generated?
> **Answer:** UTR stands for **Unique Transaction Reference**. In real UPI systems, it is a 12-digit reference used by banks and NPCI to track transfers. In our simulation, it is generated using the current date (`YYYYMMDD`) concatenated with a 4-digit unique cryptographic sequence (e.g., `202610174821`), and verified against database uniqueness constraints.
