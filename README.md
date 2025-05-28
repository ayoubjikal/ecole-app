# Système de Gestion d'Équipements Scolaires

Application moderne pour la gestion des équipements scolaires développée avec Spring Boot et Thymeleaf.

## Fonctionnalités

- **Gestion des écoles**: Ajouter, modifier, supprimer et consulter les informations des écoles
- **Gestion des équipements**: Inventorier et suivre les équipements par école
- **Gestion des suppressions**: Enregistrer et documenter les suppressions d'équipements
- **Interface moderne et réactive**: Design adapté à tous les appareils
- **Filtres et recherches**: Filtrer les équipements par date et autres critères
- **Impression**: Générer des rapports imprimables des équipements

## Technologies utilisées

- **Backend**: Java 17, Spring Boot 3.2.4
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome, JavaScript
- **Base de données**: H2 (développement), Microsoft SQL Server (production)
- **Outils de build**: Maven

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur

## Installation

1. Cloner le dépôt:
   ```bash
   git clone https://github.com/votre-utilisateur/ecole-app.git
   cd ecole-app
   ```

2. Compiler et exécuter l'application:
   ```bash
   mvn spring-boot:run
   ```

3. Accéder à l'application:
   Ouvrez un navigateur et accédez à `http://localhost:8080`

## Configuration

L'application utilise par défaut la base de données H2 en mémoire pour le développement. Pour utiliser SQL Server en production, modifiez le fichier `application.properties`:

1. Commentez la configuration H2
2. Décommentez et configurez la section SQL Server avec vos paramètres de connexion

## Structure du projet

```
src
├── main
│   ├── java
│   │   └── com.ecole.management
│   │       ├── config
│   │       ├── controller
│   │       ├── model
│   │       ├── repository
│   │       └── service
│   └── resources
│       ├── static
│       │   ├── css
│       │   ├── js
│       │   └── images
│       ├── templates
│       │   ├── ecoles
│       │   ├── equipements
│       │   ├── suppressions
│       │   └── fragments
│       └── application.properties
└── test
```

## Captures d'écran

(Ajoutez ici des captures d'écran de l'application)

## Licence

Ce projet est sous licence MIT.