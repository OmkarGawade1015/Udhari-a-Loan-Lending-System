CREATE DATABASE IF NOT EXISTS udhari_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE udhari_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS borrower_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    monthly_income DECIMAL(12,2) NOT NULL,
    employment_type VARCHAR(40) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_borrower_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_borrower_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS loans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    principal DECIMAL(12,2) NOT NULL,
    annual_interest_rate DECIMAL(8,6) NOT NULL,
    tenure_months INT NOT NULL,
    emi DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    review_remark VARCHAR(500) NULL,
    borrower_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_loans_borrower FOREIGN KEY (borrower_id) REFERENCES borrower_profiles (id),
    INDEX idx_loans_status (status),
    INDEX idx_loans_borrower (borrower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS repayments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    installment_number INT NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(16) NOT NULL,
    loan_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_repayments_loan_installment UNIQUE (loan_id, installment_number),
    CONSTRAINT fk_repayments_loan FOREIGN KEY (loan_id) REFERENCES loans (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
