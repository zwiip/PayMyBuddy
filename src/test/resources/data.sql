INSERT IGNORE INTO users (username, email, password, wallet) VALUES
('Anne', 'anne@shirley.com', '$2a$10$9XD5mdtjci3pYsaBNeaRCOuVf9busXXBo7rxcCknbRlHC0O8feKde', 100.00), -- Mot de passe: avonlea
('Matthew', 'matthew@cuthbert.com', '$2a$10$9XD5mdtjci3pYsaBNeaRCOuVf9busXXBo7rxcCknbRlHC0O8feKde', 50.00), -- Mot de passe: avonlea
('Diana', 'diana@barry.com', '$2a$10$9XD5mdtjci3pYsaBNeaRCOuVf9busXXBo7rxcCknbRlHC0O8feKde', 75.00); -- Mot de passe: avonlea

INSERT IGNORE INTO assoc_connections (user_id, connection_id) VALUES
((SELECT id FROM users WHERE email = 'anne@shirley.com'), (SELECT id FROM users WHERE email = 'matthew@cuthbert.com')),
((SELECT id FROM users WHERE email = 'anne@shirley.com'), (SELECT id FROM users WHERE email = 'diana@barry.com')),
((SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), (SELECT id FROM users WHERE email = 'diana@barry.com'));

INSERT IGNORE INTO transaction (id, sender, receiver, description, amount) VALUES
(1, (SELECT id FROM users WHERE email = 'anne@shirley.com'), (SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), 'Participation Pique Nique', 20.50), -- Anne vers Matthew
(2, (SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), (SELECT id FROM users WHERE email = 'diana@barry.com'), 'Remboursement du prêt', 15.00),  -- Matthew vers Diana
(3, (SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), (SELECT id FROM users WHERE email = 'anne@shirley.com'), 'Achat livres', 12.25), -- Matthew vers Anne
(4, (SELECT id FROM users WHERE email = 'diana@barry.com'), (SELECT id FROM users WHERE email = 'anne@shirley.com'), 'Achat fleurs', 30.00); -- Diana vers Anne