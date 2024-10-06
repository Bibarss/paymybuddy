# paymybuddy

PayMyBuddy
PayMyBuddy est une application web permettant aux utilisateurs d'effectuer des transferts d'argent entre amis de manière simple et sécurisée. Elle offre des fonctionnalités telles que l'inscription, la connexion, la gestion de profil, l'ajout de relations et la réalisation de transactions financières.

Table des matières
Fonctionnalités
Technologies utilisées
Prérequis
Installation
Configuration
Exécution de l'application
Utilisation
Structure du projet
Contribution
Licence
Auteur
Fonctionnalités
Inscription et Authentification : Les utilisateurs peuvent s'inscrire avec un nom d'utilisateur, un email et un mot de passe sécurisé. Ils peuvent ensuite se connecter pour accéder à leur compte.

Gestion du Profil : Les utilisateurs peuvent consulter et modifier leurs informations personnelles, y compris changer leur mot de passe.

Ajout de Relations : Possibilité d'ajouter d'autres utilisateurs en tant que relations pour faciliter les transferts d'argent.

Transferts d'Argent : Les utilisateurs peuvent envoyer de l'argent à leurs relations. Les transferts sont sécurisés et des frais de 0,5% sont appliqués à chaque transaction.

Historique des Transactions : Consultation des transactions passées, y compris les détails tels que le destinataire, le montant, la date et la description.

Technologies utilisées
Langage : Java 17
Framework Backend : Spring Boot 3.3.2
Spring MVC
Spring Security
Spring Data JPA (Hibernate)
Base de Données : MySQL 8.x
Moteur de Templates : Thymeleaf
Gestion de Dépendances : Maven
Serveur d'Applications : Tomcat embarqué
Autres :
Lombok pour réduire le code boilerplate
Jakarta Persistence API
Prérequis
Java Development Kit (JDK) 17 ou supérieur installé.
Maven installé pour la gestion des dépendances et la compilation du projet.
MySQL Server installé et en cours d'exécution.
Un IDE comme IntelliJ IDEA, Eclipse ou VS Code (optionnel mais recommandé).
Installation
1. Cloner le Référentiel
   bash
   Copier le code
   git clone https://github.com/votre-utilisateur/paymybuddy.git
   cd paymybuddy
2. Créer la Base de Données
   Connectez-vous à votre serveur MySQL et exécutez le script SQL pour créer la base de données et les tables nécessaires.

Script SQL :

sql
Copier le code
DROP DATABASE IF EXISTS paymybuddy;
CREATE DATABASE paymybuddy;
USE paymybuddy;

-- Table des utilisateurs
CREATE TABLE user (
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
balance DOUBLE NOT NULL DEFAULT 0.0
);

-- Table des connexions (relations)
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
3. Configurer l'Application
   Modifiez le fichier src/main/resources/application.properties pour correspondre à votre configuration locale.

properties
Copier le code
# Nom de l'application
spring.application.name=paymybuddy

# Configuration du port du serveur
server.port=8080

# Configuration du niveau de log
logging.level.root=error
logging.level.com.paymybuddy=info
logging.level.org.springframework.boot.web.embedded.tomcat=INFO

# Configuration de la source de données
spring.datasource.url=jdbc:mysql://localhost:3306/paymybuddy?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuration de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Désactiver le cache Thymeleaf en développement
spring.thymeleaf.cache=false
Remplacez your_database_username et your_database_password par vos informations de connexion MySQL.

4. Compiler le Projet
   Utilisez Maven pour compiler le projet et télécharger les dépendances.

bash
Copier le code
mvn clean install
Exécution de l'Application
Avec Maven
Vous pouvez exécuter l'application en utilisant la commande Maven suivante :

bash
Copier le code
mvn spring-boot:run
Avec votre IDE
Importez le projet Maven dans votre IDE.
Exécutez la classe com.paymybuddy.PaymybuddyApplication en tant qu'application Java.
Utilisation
Accéder à l'Application :

Ouvrez votre navigateur web et accédez à http://localhost:8080.

Inscription :

Cliquez sur "S'inscrire".
Remplissez le formulaire avec un nom d'utilisateur, une adresse email et un mot de passe.
Soumettez le formulaire pour créer votre compte.
Connexion :

Sur la page de connexion, entrez votre adresse email et votre mot de passe.
Cliquez sur "Se connecter" pour accéder à votre compte.
Gestion du Profil :

Accédez à la section "Profil" pour consulter et modifier vos informations personnelles.
Vous pouvez changer votre nom d'utilisateur, votre email et votre mot de passe.
Ajouter des Relations :

Allez dans la section "Ajouter relation".
Saisissez l'adresse email de l'utilisateur que vous souhaitez ajouter.
Une fois ajouté, cet utilisateur apparaîtra dans votre liste de relations.
Transférer de l'Argent :

Accédez à la section "Transférer".
Sélectionnez une relation dans la liste déroulante.
Entrez le montant à transférer et une description (optionnelle).
Cliquez sur "Payer" pour effectuer la transaction.
Des frais de 0,5% seront appliqués au montant transféré.
Consulter les Transactions :

Votre historique de transactions est affiché dans la section "Transférer".
Vous pouvez voir les détails de chaque transaction, y compris le destinataire, le montant et la date.
Structure du Projet
arduino
Copier le code
paymybuddy/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── paymybuddy/
│   │   │           ├── PaymybuddyApplication.java
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── controller/
│   │   │           │   └── UserController.java
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   └── Transaction.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   └── TransactionRepository.java
│   │   │           └── service/
│   │   │               ├── UserService.java
│   │   │               └── TransactionService.java
│   │   ├── resources/
│   │   │   ├── templates/
│   │   │   │   ├── home.html
│   │   │   │   ├── login.html
│   │   │   │   ├── register.html
│   │   │   │   └── profile.html
│   │   │   └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── paymybuddy/
│                   └── PaymybuddyApplicationTests.java
Contribution
Les contributions sont les bienvenues ! Si vous souhaitez améliorer l'application, veuillez suivre les étapes ci-dessous :

Fork le projet.
Créez votre branche de fonctionnalité (git checkout -b feature/ma-fonctionnalite).
Commit vos changements (git commit -m 'Ajout de ma fonctionnalité').
Push vers la branche (git push origin feature/ma-fonctionnalite).
Ouvrez une Pull Request.
Licence
Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.

Auteur
Votre Nom - Développeur - Votre Profil GitHub
Remarques
Sécurité : Assurez-vous de ne pas partager vos informations sensibles, comme vos identifiants de base de données, dans un dépôt public.
Environnement de Production : Pour un déploiement en production, pensez à configurer correctement les paramètres de sécurité et de performance.
Tests : Il est recommandé d'ajouter des tests unitaires et d'intégration pour assurer la qualité du code.
Support
Si vous rencontrez des problèmes avec l'application, veuillez ouvrir une issue sur GitHub.

Merci d'utiliser PayMyBuddy ! Nous espérons que cette application facilitera vos transferts d'argent entre amis.


