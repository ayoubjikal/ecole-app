# Configuration pour MySQL

Ce guide explique comment configurer et utiliser l'application avec MySQL.

## Prérequis

- MySQL 8.0 ou supérieur installé et en cours d'exécution
- Java 17 ou supérieur
- Maven 3.6 ou supérieur

## Configuration de la base de données MySQL

1. Assurez-vous que MySQL est en cours d'exécution
2. Créez une base de données nommée `ecole_db` (si elle n'existe pas déjà)
   ```sql
   CREATE DATABASE ecole_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. L'application est configurée par défaut pour se connecter à MySQL avec les paramètres suivants :
   - URL: `jdbc:mysql://localhost:3306/ecole_db`
   - Utilisateur: `root`
   - Mot de passe: `` (vide)

   Si vous devez modifier ces paramètres, veuillez éditer le fichier `application-prod.properties`.

## Démarrage rapide

Vous pouvez utiliser le script `run-mysql.sh` pour démarrer rapidement l'application avec MySQL :

```bash
./run-mysql.sh
```

Ce script :
1. Vérifie que MySQL est en cours d'exécution
2. Crée la base de données si elle n'existe pas
3. Compile l'application
4. Démarre l'application avec le profil de production (MySQL)

## Configuration manuelle

Si vous préférez configurer manuellement :

1. Assurez-vous que MySQL est en cours d'exécution
2. Créez la base de données `ecole_db` si elle n'existe pas
3. Compilez l'application :
   ```bash
   mvn clean package -DskipTests
   ```
4. Démarrez l'application avec le profil de production :
   ```bash
   java -jar target/management-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

## Structure de la base de données

L'application crée automatiquement les tables nécessaires grâce à JPA/Hibernate. La structure de la base de données comprend :

- `info_ecole` : Informations sur les établissements scolaires
- `equipment` : Équipements des écoles
- `suppression` : Enregistrements des suppressions d'équipements

Si vous souhaitez initialiser manuellement la base de données, vous pouvez utiliser le script SQL fourni dans `src/main/resources/db/mysql-schema.sql`.

## Données de test

L'application comprend un composant `DataInitializer` qui peut être activé pour charger des données de test. Pour l'activer, modifiez le profil actif en `dev` dans le fichier `application.properties` :

```properties
spring.profiles.active=dev
```

## Problèmes courants

1. **Erreur de connexion à MySQL**
   
   Si vous rencontrez des erreurs de connexion, vérifiez que :
   - MySQL est en cours d'exécution
   - Les identifiants sont corrects dans le fichier `application-prod.properties`
   - Le port MySQL (par défaut 3306) est accessible

2. **Erreurs de jeu de caractères**
   
   Si vous rencontrez des problèmes d'affichage de caractères spéciaux, assurez-vous que la base de données est configurée pour utiliser UTF-8 :
   ```sql
   ALTER DATABASE ecole_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Problèmes de performance**
   
   Pour améliorer les performances en production, vous pouvez ajuster les paramètres de connexion dans `application-prod.properties`, par exemple :
   ```properties
   spring.datasource.hikari.maximum-pool-size=10
   spring.datasource.hikari.minimum-idle=5
   spring.datasource.hikari.idle-timeout=30000
   ```