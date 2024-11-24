-- Create the database
CREATE DATABASE bankdatabase;

-- Use the newly created database
USE bankdatabase;

-- Create the accounts table
CREATE TABLE accounts (
    account_number INT PRIMARY KEY,   -- Account number as primary key
    name VARCHAR(100),                -- Name of the account holder
    balance DECIMAL(10, 2)            -- Account balance with two decimal points for precision
);

-- Create the transactions table
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number INT,
    transaction_type VARCHAR(20),  -- Adjust length if necessary
    amount DECIMAL(10, 2),
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
