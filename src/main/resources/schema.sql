CREATE DATABASE IF NOT EXISTS pay_my_buddy;
USE pay_my_buddy;

CREATE TABLE IF NOT EXISTS users
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    wallet DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS assoc_connections
(
    user_id INT NOT NULL,
    connection_id INT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (connection_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS transaction
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sender INT NOT NULL,
    receiver INT NOT NULL,
    description VARCHAR(200),
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sender) REFERENCES users (id),
    FOREIGN KEY (receiver) REFERENCES users (id)
);