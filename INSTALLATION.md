## Installation et lancement

### Prérequis

- Java JDK 25
- Maven (Maven Wrapper inclus dans le projet)
- VS Code ou un autre IDE Java

### 1. Démarrer l'application

Depuis la racine du projet, exécuter :

```powershell
.\mvnw.cmd spring-boot:run
```

L'API est ensuite accessible à l'adresse : `http://localhost:8080`

### 2. Charger les données de démonstration (fixtures)

Les fixtures sont automatiquement chargées au démarrage de l'application grâce à `DataInitializer`, qui implémente `CommandLineRunner`.

Aucune commande supplémentaire n'est nécessaire.

Les données initialisées comprennent :

- 8 livres de différents genres, dont un possédant un seul exemplaire.
- 3 adhérents fictifs.
- Des identifiants générés automatiquement.
- Des dates d'inscription générées automatiquement.

Pour vérifier le chargement des données :

- `GET http://localhost:8080/api/books`
- `GET http://localhost:8080/api/members`

**Note :** les données sont stockées uniquement en mémoire. Elles sont donc réinitialisées à chaque redémarrage de l'application.

### 3. Lancer les tests automatisés

Les tests d'intégration utilisent **JUnit 5 et Spring MockMvc** pour vérifier les endpoints REST, les codes HTTP et les principales règles métier.

Depuis la racine du projet :

```powershell
.\mvnw.cmd test
```

Les tests démarrent automatiquement un contexte Spring Boot : il n'est pas nécessaire de lancer le serveur au préalable.

Les scénarios testés couvrent notamment :

- La récupération et le filtrage des livres.
- La création et la validation des ressources.
- L'unicité des ISBN et des emails.
- Les emprunts et leurs restrictions.
- La mise à jour des stocks et les retours.
- Les codes HTTP de succès et d'erreur.

Une exécution réussie affiche notamment :

```text
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 4. Configuration

La configuration se trouve dans `src/main/resources/application.properties`.

La limite d'emprunts simultanés par adhérent est définie par la propriété :

```properties
library.maxLoansPerMember=3
```