
# PayMyBuddy

**PayMyBuddy** est une application web facilitant les transferts d'argent entre amis de manière simple et sécurisée.

## Fonctionnalités

- **Inscription & Connexion** : Créez un compte et connectez-vous pour utiliser l'application.
- **Gestion du Profil** : Modifiez vos informations personnelles et votre mot de passe.
- **Ajout de Relations** : Ajoutez des amis à votre liste de relations pour effectuer des transferts.
- **Transferts d'Argent** : Envoyez de l'argent à vos relations avec des frais de 0,5 % par transaction.
- **Historique des Transactions** : Consultez vos transactions passées et leurs détails.

## Technologies Utilisées

- **Langage** : Java 17
- **Framework Backend** : Spring Boot 3.3.2 (Spring MVC, Spring Security, Spring Data JPA)
- **Base de Données** : MySQL 8.x ou PostgreSQL
- **Moteur de Templates** : Thymeleaf
- **Gestion de Dépendances** : Maven
- **Serveur d'Applications** : Tomcat embarqué
- **Autres** :
    - Lombok pour réduire le code boilerplate
    - Jakarta Persistence API

## Prérequis

Avant d'installer et d'exécuter l'application, assurez-vous d'avoir les éléments suivants installés :

- **JDK 17** ou version plus récente
- **Maven** pour la gestion des dépendances et la compilation
- **Un SGBD (Système de Gestion de Base de Données)** : MySQL ou PostgreSQL

## Installation

### 1. Cloner le Référentiel

```bash
git clone https://github.com/votre-utilisateur/paymybuddy.git
cd paymybuddy
```

### 2. Créer la Base de Données

Le script SQL nécessaire à la création de la base de données et des tables se trouve dans le répertoire `doc` du projet sous le nom `db_schema.sql`. Ce script configure les tables utilisateurs, connexions et transactions. Connectez-vous à votre serveur MySQL (ou PostgreSQL) et exécutez le script pour préparer votre base de données.

### 3. Configuration de l'Application

Modifiez le fichier `src/main/resources/application.properties` pour configurer les paramètres de votre base de données. Exemple pour MySQL :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/paymybuddy
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
spring.jpa.hibernate.ddl-auto=update
```

Remplacez `your_database_username` et `your_database_password` par vos informations.

### 4. Compiler et Exécuter le Projet

Une fois la configuration terminée, compilez et exécutez le projet :

```bash
mvn clean install
mvn spring-boot:run
```

Ouvrez ensuite votre navigateur et accédez à [http://localhost:8080](http://localhost:8080) pour utiliser l'application.

## Utilisation

### Inscription

- Cliquez sur "S'inscrire" pour créer un nouveau compte avec un nom d'utilisateur, une adresse email et un mot de passe sécurisé.

### Connexion

- Entrez votre adresse email et votre mot de passe pour vous connecter.

### Gestion du Profil

- Consultez et modifiez vos informations personnelles (nom d'utilisateur, email, mot de passe) dans la section "Profil".

### Ajouter des Relations

- Dans la section "Ajouter relation", entrez l'adresse email d'une personne pour l'ajouter à vos relations.

### Transférer de l'Argent

- Sélectionnez une relation et entrez le montant que vous souhaitez transférer avec une description (optionnelle).
- Des frais de 0,5 % sont appliqués à chaque transaction.

## Structure du Projet

```
paymybuddy/
├── doc/
│   ├── db_schema.sql        # Script SQL pour créer la base de données et les tables
│   ├── presentation.pdf     # Présentation sous forme de slides sur l'application
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── paymybuddy/
    │   │           ├── PaymybuddyApplication.java
    │   │           ├── config/
    │   │           │   └── SecurityConfig.java
    │   │           ├── controller/
    │   │           │   └── UsersController.java
    │   │           │   └── TransactionController.java
    │   │           ├── entity/
    │   │           │   ├── Users.java
    │   │           │   └── Transaction.java
    │   │           ├── repository/
    │   │           │   ├── UsersRepository.java
    │   │           │   └── TransactionRepository.java
    │   │           └── service/
    │   │               ├── UsersService.java
    │   │               └── TransactionService.java
    ├── resources/
    │   ├── templates/
    │   │   ├──static.css/
    │   │   │   ├── style.css
    │   │   │   └── styleHome.css
    │   │   ├── login.html
    │   │   ├── addConnection.html
    │   │   ├── register.html
    │   │   ├── transfer.html
    │   │   └── profile.html
    │   └── application.properties
    └── test/
        ├── java/
        │   └── com/
        │       └── paymybuddy/
        │           ├── PaymybuddyApplicationTests.java
        │           ├── controller/
        │           │   └── UsersControllerTests.java
        │           │   └── TransactionControllerTests.java
        │           ├── repository/
        │           │   ├── UsersRepositoryTests.java
        │           │   └── TransactionRepositoryTests.java
        │           └── service/
        │               ├── UsersServiceTests.java
        │               └── TransactionServiceTests.java
        └── resources/
           ├── application-test.properties
           ├── data.sql
           └── schema.sql


## Documentation

- Le script SQL `db_schema.sql` est disponible dans le répertoire `doc` pour la création de la base de données.
- Une présentation sous forme de slides (`presentation.pdf`) détaillant les fonctionnalités et l'architecture de l'application est également incluse dans le répertoire `doc`.

## Contribution

Les contributions sont les bienvenues ! Ouvrez une pull request ou créez une issue pour toute suggestion d'amélioration.

## Licence

Ce projet est sous licence MIT. Consultez le fichier `LICENSE` pour plus de détails.

## Auteur

Votre nom – Développeur
