Drop table IF EXISTS transaction;
Drop table IF EXISTS user_connections;
Drop table IF EXISTS user;

-- Table des utilisateurs
CREATE TABLE user (
id INT AUgitTO_INCREMENT PRIMARY KEY,
username VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL
);

-- Table des connexions (associations d’amis)
CREATE TABLE user_connections (
user_id INT,
connection_id INT,
PRIMARY KEY (user_id, connection_id),
CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES User(id),
CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES User(id)
);

-- Table des transactions
CREATE TABLE transaction (
id INT AUTO_INCREMENT PRIMARY KEY,
sender_id INT NOT NULL,
receiver_id INT NOT NULL,
description VARCHAR(255),
amount DOUBLE NOT NULL,
date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES User(id),
CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES User(id)
);