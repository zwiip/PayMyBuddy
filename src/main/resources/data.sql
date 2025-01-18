INSERT IGNORE INTO users (username, email, password, wallet) VALUES
('Anne', 'anne@shirley.com', '$2a$10$VcjUbsEz1pMj3E5G0ddB/.t9Xz8KcYchMIc.YamWzRn58v6tDMyvC', 100.00), -- Mot de passe: password
('Matthew', 'matthew@cuthbert.com', '$2a$10$8Lf8NNLsff.PF5.J6zGqTuUPcWUnLrRbGH8OoBya.jIBbtI8.xX8C', 50.00), -- Mot de passe: password
('Diana', 'diana@barry.com', '$2a$10$A8S7gJQUw4.RD9W9ZZiY9uQ4pJLsE5KmdJK3YH/E0I/7KdpNUEVcO', 75.00); -- Mot de passe: password

INSERT IGNORE INTO assoc_connections (user_id, connection_id) VALUES
((SELECT id FROM users WHERE email = 'anne@shirley.com'), (SELECT id FROM users WHERE email = 'matthew@cuthbert.com')),
((SELECT id FROM users WHERE email = 'anne@shirley.com'), (SELECT id FROM users WHERE email = 'diana@barry.com')),
((SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), (SELECT id FROM users WHERE email = 'diana@barry.com'));

INSERT IGNORE INTO transaction (sender, receiver, description, amount) VALUES
((SELECT id FROM users WHERE email = 'anne@shirley.com'), (SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), 'Participation Pique Nique', 20.50), -- Anne vers Matthew
((SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), (SELECT id FROM users WHERE email = 'diana@barry.com'), 'Remboursement du prêt', 15.00),  -- Matthew vers Diana
((SELECT id FROM users WHERE email = 'matthew@cuthbert.com'), (SELECT id FROM users WHERE email = 'anne@shirley.com'), 'Achat livres', 12.25), -- Matthew vers Anne
((SELECT id FROM users WHERE email = 'diana@barry.com'), (SELECT id FROM users WHERE email = 'anne@shirley.com'), 'Achat fleurs', 30.00); -- Diana vers Anne