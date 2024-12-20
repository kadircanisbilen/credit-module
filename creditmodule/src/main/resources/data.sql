INSERT INTO users (id, name, surname, username, password, role, credit_limit, used_credit_limit)
VALUES (1, 'Admin', 'User', 'admin', '$2a$10$SgBLkzk17g.VbZtu2CJVIebHtifChNfiLdIK5bD2.xDBwb7XnX89O', 'ADMIN', 0.00, 0.00),
       (2, 'John', 'Doe', 'john_doe', '$2a$10$wXqhUS.3aU6n4Ohcd3xpLeToOSR0cOuoy703Log/AWy68JuVrRQbK', 'CUSTOMER', 20000.00, 5000.00),
       (3, 'Jane', 'Smith', 'jane_smith', '$2a$10$wXqhUS.3aU6n4Ohcd3xpLeToOSR0cOuoy703Log/AWy68JuVrRQbK', 'CUSTOMER', 15000.00, 2000.00);

INSERT INTO loan (id, user_id, loan_amount, number_of_installments, interest_rate, is_paid, create_date)
VALUES (1, 2, 8000.00, 12, 0.15, FALSE, '2024-01-01'),
       (2, 2, 10000.00, 24, 0.20, TRUE, '2023-01-01'),
       (3, 3, 5000.00, 6, 0.10, FALSE, '2024-02-01');

INSERT INTO loan_installment (id, loan_id, amount, paid_amount, due_date, payment_date, is_paid)
VALUES (1, 1, 700.00, 700.00, '2024-02-01', '2024-02-01', TRUE),
       (2, 1, 700.00, 0.00, '2024-03-01', NULL, FALSE),
       (3, 1, 700.00, 0.00, '2024-04-01', NULL, FALSE),
       (4, 2, 416.67, 416.67, '2023-02-01', '2023-02-01', TRUE),
       (5, 2, 416.67, 416.67, '2023-03-01', '2023-03-01', TRUE),
       (6, 3, 833.33, 0.00, '2024-03-01', NULL, FALSE);
