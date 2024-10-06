DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS user_connections;
DROP TABLE IF EXISTS user;

-- Table des utilisateurs
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    balance DOUBLE NOT NULL DEFAULT 0.0
);

-- Table des connexions (associations d’amis)
CREATE TABLE user_connections (
    user_id INT,
    connection_id INT,
    PRIMARY KEY (user_id, connection_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES user(id)
);

-- Table des transactions
CREATE TABLE transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    description VARCHAR(255),
    amount DOUBLE NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES user(id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES user(id)
);
