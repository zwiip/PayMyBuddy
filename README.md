# Pay My Buddy

## Contexte

*Ce projet est le 6ème de la formation Développement d'applications Java d'OpenClassrooms.*

Pay My Buddy est une application Spring Boot qui permet aux utilisateurs de gérer des transactions entre amis plus simplement qu'avec les virements bancaires classiques.
Le projet respecte les bonnes pratiques de développement logiciel, de sécurité et d'accessibilité.

## Technologies utilisées

- Back-end : Java, Spring Boot (Spring Data JPA, Spring Security).
- Base de données : MySQL pour l'environnement de production, H2 pour les tests.
- Front-end : Thymeleaf pour les vues HTML/CSS dynamiques.

## Prérequis

- Java : Version 17 ou supérieure.
- Maven : Pour la gestion des dépendances.
- MySQL : Une instance locale ou distante.

## Configuration

Deux solutions pour initialiser et peupler la base de données.

### 1. Lancement automatique avec application.properties

L'application est paramétrée pour créer automatiquement la base de données en utilisant les fichiers schema.sql et data.sql ci-dessous.

**Il vous suffit pour cela de spécifier votre nom d'utilisateur et mot de passe** dans le fichier src/main/resources/application.properties :

```
spring.datasource.username=<votre_nom_d'utilisateur>
spring.datasource.password=<votre_mot_de_passe>
```

Notez que si vous avez déjà initialisé la base cela ne posera pas de soucis, les tables déjà existantes ne seront pas recréées et seules les données manquantes seront remises. Les données supplémentaires que vous avez pu créer ne seront pas supprimées.

### 2. Initialisation de la base de données

L'application utilise deux fichiers pour créer et peupler la base de données :
Ces fichiers sont placés dans le répertoire src/main/resources.

#### **schema.sql :** Création des tables.
```
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
sender INT,
receiver INT,
description VARCHAR(200),
amount DECIMAL(10,2) NOT NULL,
FOREIGN KEY (sender) REFERENCES users (id) ON DELETE SET NULL,
FOREIGN KEY (receiver) REFERENCES users (id) ON DELETE SET NULL
);
```

#### **data.sql :** Insertion des données initiales.

```
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
```

## Lancement de l'application
Prérequis : avoir spécifié le mot de passe de sa base MySql dans ``application.properties``.
(voir section configuration)

**1. Compilation et lancement**
```
mvn clean install
mvn spring-boot:run
```

**2. Accès à l'application**
Ouvrez un navigateur et accédez à ``http://localhost:8080``

## Tests et rapports

L'application est couverte par des tests afin de vérifier le bon fonctionnement des services.

- Pour lancer les tests : ``mvn test``

- Les logs des tests échoués ou réussis sont dans le dossier : ``target/surefire-reports``

- Afin de vérifier le taux de couverture des tests avec JaCoCo, vous pouvez lancer la commande : ``mvn verify``

- Les rapports JaCoCo seront générés dans le dossier : ``target/site/jacoco/index.html``