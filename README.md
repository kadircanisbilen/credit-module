# Credit Module API - Documentation

## Project Overview
The Credit Module API is a backend service developed for a bank to manage customer loans. It provides endpoints to create loans, list loans, pay installments, and more. The project is designed to ensure security, scalability, and maintainability.

## Key Features
- Role-based authorization (ADMIN and CUSTOMER roles).
- Ability to create loans with validation (installments, interest rate, credit limit).
- List loans and installments based on user roles.
- Early payment discounts and late payment penalties.

## Technologies Used
- **Java 21**
- **Spring Boot**
- **H2 Database**
- **Spring Security with JWT**
- **Maven**
- **Swagger UI**
- **JUnit** for testing

---

## Setup Instructions

### Prerequisites
1. Java 21 or higher
2. Maven

### Steps to Run
1. Clone the repository:
   ```
   git clone <repository_url>
   cd creditmodule
   ```
2. Build the project:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```
4. Access the following resources:
    - **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    - **H2 Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## Environment Variables
To ensure security, set the following environment variable before running the application:

```yaml
jwt:
  secret:
     key: ${JWT_SECRET_KEY:defaultSecretKey}
```

- Replace `defaultSecretKey` with a secure key or set the `JWT_SECRET_KEY` system variable.

---

## Database Details

### Tables
1. **users**
    - `id`: Primary key
    - `name`: First name
    - `surname`: Last name
    - `username`: Unique username
    - `password`: Encrypted password
    - `role`: Role of the user (ADMIN/CUSTOMER)
    - `credit_limit`: Total credit limit
    - `used_credit_limit`: Used credit limit

2. **loan**
    - `id`: Primary key
    - `user_id`: Foreign key to `users`
    - `loan_amount`: Total loan amount
    - `number_of_installments`: Number of installments
    - `interest_rate`: Interest rate
    - `is_paid`: Loan status
    - `create_date`: Loan creation date

3. **loan_installment**
    - `id`: Primary key
    - `loan_id`: Foreign key to `loan`
    - `amount`: Installment amount
    - `paid_amount`: Amount paid
    - `due_date`: Installment due date
    - `payment_date`: Payment date
    - `is_paid`: Payment status

---

## API Endpoints

### Authentication
1. **Login**
    - **URL:** `POST /api/auth/login`
    - **Request Body:**
      ```json
      {
        "username": "admin",
        "password": "admin123"
      }
      ```
    - **Response:**
      ```json
      {
        "token": "<JWT_TOKEN>",
        "roles": ["ROLE_ADMIN"]
      }
      ```

### Admin Endpoints
2. **Create Loan**
    - **URL:** `POST /api/admin/loans`
    - **Authorization:** Admin only
    - **Request Body:**
      ```json
      {
        "userId": 1,
        "loanAmount": 10000,
        "interestRate": 0.2,
        "numberOfInstallments": 12
      }
      ```
    - **Response:**
      ```json
      {
        "loanId": 1,
        "loanAmount": 12000,
        "numberOfInstallments": 12,
        "isPaid": false
      }
      ```

3. **List All Loans**
    - **URL:** `GET /api/admin/loans`
    - **Authorization:** Admin only

### Customer Endpoints
4. **List User Loans**
    - **URL:** `GET /api/customer/loans`
    - **Authorization:** Customer only

5. **Pay Loan Installments**
    - **URL:** `POST /api/customer/pay-loan`
    - **Authorization:** Customer only
    - **Request Body:**
      ```json
      {
        "loanId": 1,
        "amount": 2000
      }
      ```

---

## Bonus Features
1. **Role-Based Authorization:**
    - Admin users can perform actions for all users.
    - Customers can only access and manage their own loans.

2. **Reward and Penalty System:**
    - **Early Payment Discount:** 0.001 * (days before due date).
    - **Late Payment Penalty:** 0.001 * (days after due date).

---

## Testing
1. Run tests:
   ```
   mvn test
   ```
2. Test Coverage:
    - Unit tests for services.

---

## Future Enhancements
- Add email notifications for payment reminders. (Optional)
- Integrate with a real database like PostgreSQL or MySQL.
- Add more filters for listing loans and installments.

---

## Contact

- Email: kadircanisbilen@gmail.com
- GitHub: [GitHub Profile](https://github.com/kadircanisbilen)
