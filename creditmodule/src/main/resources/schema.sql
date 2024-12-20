CREATE TABLE IF NOT EXISTS users
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100)        NOT NULL,
    surname           VARCHAR(100)        NOT NULL,
    credit_limit      DECIMAL(15, 2)      NOT NULL,
    used_credit_limit DECIMAL(15, 2) DEFAULT 0,
    username          VARCHAR(255) UNIQUE NOT NULL,
    password          VARCHAR(255)        NOT NULL,
    role              VARCHAR(20)         NOT NULL
);

CREATE TABLE IF NOT EXISTS loan
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                BIGINT         NOT NULL,
    loan_amount            DECIMAL(15, 2) NOT NULL,
    number_of_installments INT            NOT NULL,
    interest_rate          DECIMAL(5, 3)  NOT NULL,
    is_paid                BOOLEAN DEFAULT FALSE,
    create_date            DATE           NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS loan_installment
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id      BIGINT         NOT NULL,
    amount       DECIMAL(15, 2) NOT NULL,
    paid_amount  DECIMAL(15, 2) DEFAULT 0,
    due_date     DATE           NOT NULL,
    payment_date DATE           DEFAULT NULL,
    is_paid      BOOLEAN        DEFAULT FALSE,
    FOREIGN KEY (loan_id) REFERENCES loan (id) ON DELETE CASCADE
);
