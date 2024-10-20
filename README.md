# paymybuddy

PayMyBuddy
PayMyBuddy est une application web permettant aux utilisateurs d'effectuer des transferts d'argent entre amis de manière simple et sécurisée. Elle offre des fonctionnalités telles que l'inscription, la connexion, la gestion de profil, l'ajout de relations et la réalisation de transactions financières.

Table des matières : <br>
1. Fonctionnalités<br>
2. Technologies utilisées<br>
3. Prérequis<br>
4. Installation<br>
5. Configuration<br>
6. Exécution de l'application<br>
7. Utilisation<br>
8. Structure du projet<br>
9. Contribution<br>
10. Licence<br>
11. Auteur<br><br>

__Fonctionnalités__<br>
_Inscription et Authentification_ : Les utilisateurs peuvent s'inscrire avec un nom d'utilisateur, un email et un mot de passe sécurisé. Ils peuvent ensuite se connecter pour accéder à leur compte.

_Gestion du Profil_ : Les utilisateurs peuvent consulter et modifier leurs informations personnelles, y compris changer leur mot de passe.

_Ajout de Relations_ : Possibilité d'ajouter d'autres utilisateurs en tant que relations pour faciliter les transferts d'argent.

_Transferts d'Argent_ : Les utilisateurs peuvent envoyer de l'argent à leurs relations. Les transferts sont sécurisés et des frais de 0,5% sont appliqués à chaque transaction.

_Historique des Transactions_ : Consultation des transactions passées, y compris les détails tels que le destinataire, le montant, la date et la description.

__Technologies utilisées__
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

<br>
__Prérequis__
Java Development Kit (JDK) 17 ou supérieur installé.
Maven installé pour la gestion des dépendances et la compilation du projet.
MySQL Server installé et en cours d'exécution.
Un IDE comme IntelliJ IDEA, Eclipse ou VS Code (optionnel mais recommandé).
__Installation__
1. Cloner le Référentiel
   bash
   Copier le code
   git clone https://github.com/votre-utilisateur/paymybuddy.git
   cd paymybuddy
2. Créer la Base de Données
   Connectez-vous à votre serveur MySQL et exécutez le script SQL pour créer la base de données et les tables nécessaires.

Script SQL :

sql
<br>Copier le code
<br>DROP DATABASE IF EXISTS paymybuddy;
<br>CREATE DATABASE paymybuddy;
<br>USE paymybuddy;

<br>-- Table des utilisateurs
<br>CREATE TABLE user (
<br>id INT AUTO_INCREMENT PRIMARY KEY,
<br>username VARCHAR(255) NOT NULL,
<br>email VARCHAR(255) NOT NULL UNIQUE,
<br>password VARCHAR(255) NOT NULL,
<br>balance DOUBLE NOT NULL DEFAULT 0.0
<br>);

<br>-- Table des connexions (relations)
<br>CREATE TABLE user_connections (
<br>user_id INT,
<br>connection_id INT,
<br>PRIMARY KEY (user_id, connection_id),
<br>CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id),
<br>CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES user(id)
<br>);

<br>-- Table des transactions
<br>CREATE TABLE transaction (
<br>id INT AUTO_INCREMENT PRIMARY KEY,
<br>sender_id INT NOT NULL,
<br>receiver_id INT NOT NULL,
<br>description VARCHAR(255),
<br>amount DOUBLE NOT NULL,
<br>date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
<br>CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES user(id),
<br>CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES user(id)
<br>); 

<br>__Configurer l'Application__

   Modifiez le fichier src/main/resources/application.properties pour correspondre à votre configuration locale.

Exemple : Remplacez your_database_username et your_database_password par vos informations de connexion MySQL.

__Compiler le Projet__

Utilisez Maven pour compiler le projet et télécharger les dépendances.
Ouvrez votre navigateur web et accédez à http://localhost:8080.

__Inscription :__

Cliquez sur "S'inscrire".
<br>Remplissez le formulaire avec un nom d'utilisateur, une adresse email et un mot de passe.
<br>Soumettez le formulaire pour créer votre compte.

<br>Connexion :

<br>Sur la page de connexion, entrez votre adresse email et votre mot de passe.
<br>Cliquez sur "Se connecter" pour accéder à votre compte.
<br><br>Gestion du Profil :

Accédez à la section "Profil" pour consulter et modifier vos informations personnelles.
Vous pouvez changer votre nom d'utilisateur, votre email et votre mot de passe.
<br><br>Ajouter des Relations :

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

<br>
__Structure du Projet__
arduino
Copier le code

paymybuddy/
<br>├── pom.xml
<br>├── src/
<br>│   ├── main/
<br>│   │   ├── java/
<br>│   │   │   └── com/
<br>│   │   │       └── paymybuddy/
<br>│   │   │           ├── PaymybuddyApplication.java
<br>│   │   │           ├── config/
<br>│   │   │           │   └── SecurityConfig.java
<br>│   │   │           ├── controller/
<br>│   │   │           │   └── UserController.java
<br>│   │   │           ├── model/
<br>│   │   │           │   ├── User.java
<br>│   │   │           │   └── Transaction.java
<br>│   │   │           ├── repository/
<br>│   │   │           │   ├── UserRepository.java
<br>│   │   │           │   └── TransactionRepository.java
<br>│   │   │           └── service/
<br>│   │   │               ├── UserService.java
<br>│   │   │               └── TransactionService.java
<br>│   │   ├── resources/
<br>│   │   │   ├── templates/
<br>│   │   │   │   ├── login.html
<br>│   │   │   │   ├── register.html
<br>│   │   │   │   └── profile.html
<br>│   │   │   └── application.properties
<br>│   └── test/
<br>│       └── java/
<br>│           └── com/
<br>│               └── paymybuddy/
<br>│                   └── PaymybuddyApplicationTests.java
<br>
<br>
__Contribution__
Les contributions sont les bienvenues ! Si vous souhaitez améliorer l'application, veuillez suivre les étapes ci-dessous :

Fork le projet.
Créez votre branche de fonctionnalité (git checkout -b feature/ma-fonctionnalite).
Commit vos changements (git commit -m 'Ajout de ma fonctionnalité').
Push vers la branche (git push origin feature/ma-fonctionnalite).
Ouvrez une Pull Request.

__Licence__
Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.

__Auteur__
Votre Nom - Développeur - Votre Profil GitHub
Remarques
Sécurité : Assurez-vous de ne pas partager vos informations sensibles, comme vos identifiants de base de données, dans un dépôt public.
Environnement de Production : Pour un déploiement en production, pensez à configurer correctement les paramètres de sécurité et de performance.
Tests : Il est recommandé d'ajouter des tests unitaires et d'intégration pour assurer la qualité du code.
Support
Si vous rencontrez des problèmes avec l'application, veuillez ouvrir une issue sur GitHub.

Merci d'utiliser PayMyBuddy ! Nous espérons que cette application facilitera vos transferts d'argent entre amis.


