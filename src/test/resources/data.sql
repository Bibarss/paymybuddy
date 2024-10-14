
-- tests u

INSERT INTO users (username, email, password, balance)
VALUES ('JohnDoe', 'johndoe@example.com', '123xx*', 100);

INSERT INTO users (username, email, password, balance)
VALUES ('TotoTiti', 'tototiti@example.com', 'xxxxx*', 50);


-- Tests u TransactionRepositoryTest


INSERT INTO users (username, email, password, balance)
VALUES ('SenderUser', 'sender@example.com', 'password123', 100);

INSERT INTO users (username, email, password, balance)
VALUES ('ReceiverUser', 'receiver@example.com', 'password456*', 50);

INSERT INTO user_connections (user_id, connection_id)
VALUES (3, 4);

INSERT INTO transaction (sender_id,  receiver_id, description, amount, date)
VALUES (3, 4, 'Transaction 1', 100, '2024-10-09 00:00:00');

INSERT INTO transaction (sender_id,  receiver_id, description, amount, date)
VALUES (3, 4, 'Transaction 2', 100,  '2024-10-09 00:00:00');

-- tests d'integration
-- Insérer des utilisateurs de test
INSERT INTO users (username, email, password, balance) VALUES
('User1', 'user1@example.com', 'password1', 500.00),
('User2', 'user2@example.com', 'password2', 300.00),
('User3', 'user3@example.com', 'password2', 300.00);

INSERT INTO user_connections (user_id, connection_id)
VALUES (5, 6);

INSERT INTO user_connections (user_id, connection_id)
VALUES (6, 5);

-- Insérer des transactions de test
INSERT INTO transaction (sender_id, receiver_id, description, amount) VALUES
(5, 6, 'Transaction 1', 100.0),
(6, 5, 'Transaction 2', 50.0);
