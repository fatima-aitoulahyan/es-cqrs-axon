# Event Sourcing et CQRS avec Axon Framework et Axon Server

Application de gestion de comptes bancaires illustrant l'implémentation pratique des patterns d'architecture **Event Sourcing** et **CQRS** (Command Query Responsibility Segregation) avec **Spring Boot**, **Axon Framework** et **Axon Server**.

Ce projet correspond à l'Activité Pratique N°4 : « CQRS et Event Sourcing avec Axon et Spring Cloud » (Mohamed YOUSSFI).

---

## Table des matières

1. [Objectifs](#1-objectifs)
2. [Fonctionnalités](#2-fonctionnalités)
3. [Concepts clés](#3-concepts-clés)
4. [Architecture](#4-architecture)
5. [Stack technique](#5-stack-technique)
6. [Structure du projet](#6-structure-du-projet)
7. [Description de l'implémentation](#7-description-de-limplémentation)
8. [Prérequis](#8-prérequis)
9. [Installation et lancement](#9-installation-et-lancement)
10. [API REST](#10-api-rest)
11. [Démonstration et résultats](#11-démonstration-et-résultats)
12. [Évolution de l'Event Store](#12-évolution-de-levent-store)
13. [Conclusion](#13-conclusion)
14. [Auteur](#14-auteur)

---

## 1. Objectifs

- Comprendre et mettre en pratique les patterns **Event Sourcing** et **CQRS**.
- Séparer clairement le modèle d'écriture (Command Side) du modèle de lecture (Query Side).
- Persister l'historique complet des changements d'état sous forme d'événements immuables dans un **Event Store**.
- Projeter les événements dans une base relationnelle dédiée à la lecture.
- Diffuser les opérations en temps réel vers le front-end grâce aux **Server-Sent Events (SSE)** et à **Project Reactor**.
- Passer d'un Event Store local (mémoire, puis PostgreSQL) à un broker d'événements distribué, **Axon Server**, communiquant via gRPC.

## 2. Fonctionnalités

- Création d'un compte bancaire avec un solde initial et une devise.
- Activation automatique du compte après sa création (événement `AccountActivatedEvent`).
- Opérations de crédit et de débit avec contrôle des règles métier.
- Modification du statut d'un compte (par exemple `SUSPENDED`).
- Consultation de la liste des comptes et du relevé d'un compte (historique des opérations).
- Consultation de l'historique des événements d'un compte.
- Suivi en temps réel des opérations d'un compte via SSE.

**Règles métier vérifiées par l'agrégat :**

- Le solde initial doit être positif.
- Le montant d'une opération doit être positif.
- Le compte doit être activé pour effectuer des opérations.
- Le solde doit être suffisant pour effectuer un débit.

## 3. Concepts clés

### Event Sourcing

L'état d'une entité n'est pas stocké directement. Seule la **suite des événements** qui l'ont fait évoluer est persistée dans l'Event Store. L'état courant est reconstruit en rejouant ces événements dans l'ordre.

### CQRS

Le pattern CQRS sépare les responsabilités :

| Côté | Rôle | Composants principaux |
|------|------|-----------------------|
| **Command Side** | Opérations d'écriture | `CommandGateway`, commandes, `AccountAggregate`, événements |
| **Query Side** | Opérations de lecture | `QueryGateway`, requêtes, `AccountQueryHandler`, projections JPA |

### Fonction de décision et fonction d'évolution

- **Fonction de décision** (`@CommandHandler`) : vérifie les règles métier et émet un événement via `AggregateLifecycle.apply()`.
- **Fonction d'évolution** (`@EventSourcingHandler`) : applique l'événement à l'agrégat et met à jour son état interne.

## 4. Architecture

L'architecture suit les principes CQRS et Event Sourcing. Les trois bus (Command Bus, Query Bus, Event Bus) assurent le routage des messages entre les composants.

![Architecture CQRS et Event Sourcing avec Axon](docs/images/img12.png)

### Flux côté commande

1. Le contrôleur REST (`AccountCommandController`) reçoit la requête.
2. Il transmet une commande via le `CommandGateway` (méthode `send`).
3. Le Command Bus achemine la commande vers l'`AccountAggregate`.
4. Le `@CommandHandler` vérifie les règles métier et applique un événement.
5. L'événement est enregistré dans l'Event Store.
6. Le `@EventSourcingHandler` met à jour l'état de l'agrégat.

### Flux côté lecture

1. L'événement est publié sur l'Event Bus.
2. L'`AccountEventHandler` (`@EventHandler`) projette l'événement dans la base de lecture (entités `Account` et `AccountOperation`).
3. Les requêtes de consultation passent par le `QueryGateway`.
4. L'`AccountQueryHandler` (`@QueryHandler`) lit les données projetées et retourne les DTO.

### Temps réel (SSE)

Le `QueryUpdateEmitter` pousse chaque nouvelle opération (crédit ou débit) vers un `Flux<AccountOperation>` exposé par l'endpoint `watch`, consommable en temps réel par le client Web.

## 5. Stack technique

| Technologie | Utilisation |
|-------------|-------------|
| Java 21 | Langage |
| Spring Boot | Framework applicatif |
| Spring Web | API REST |
| Spring Data JPA | Persistance du modèle de lecture |
| Axon Framework | Event Sourcing et CQRS |
| Axon Server | Event Store et broker de messages (gRPC) |
| PostgreSQL | Base de données (Event Store et modèle de lecture) |
| H2 | Base en mémoire (phase initiale) |
| PgAdmin | Administration PostgreSQL |
| Project Reactor | Flux réactifs et SSE |
| Jackson | Sérialisation JSON des événements |
| Lombok | Réduction du code répétitif |
| SpringDoc OpenAPI | Documentation et tests via Swagger UI |
| Docker Compose | Orchestration des services |

## 6. Structure du projet

```
es-cqrs-axon
├── docs
│   └── images                      # Captures d'écran utilisées dans ce README
├── src
│   └── main
│       ├── java/org/example/eventsourcingcqrsaxon
│       │   ├── commands
│       │   │   ├── aggregates      # AccountAggregate
│       │   │   ├── commands        # Commandes (Add, Credit, Debit, UpdateStatus)
│       │   │   ├── controllers     # AccountCommandController
│       │   │   └── dto             # Objets de transfert des requêtes
│       │   ├── events              # Événements du domaine
│       │   └── query
│       │       ├── controllers     # AccountQueryController
│       │       ├── entities        # Account, AccountOperation
│       │       ├── handlers        # AccountEventHandler, AccountQueryHandler
│       │       ├── queries         # GetAllAccountsQuery, GetAccountStatementQuery
│       │       └── repositories    # AccountRepository, AccountOperationRepository
│       └── resources
│           └── application.properties
├── docker-compose.yml              # PostgreSQL, PgAdmin, Axon Server
└── pom.xml
```

> L'organisation exacte des packages peut légèrement varier ; elle suit le découpage `controllers`, `commands`, `dto`, `aggregates`, `events` du côté commande, puis le côté lecture.

## 7. Description de l'implémentation

### 7.1 Commandes

Classes immuables annotées avec `@TargetAggregateIdentifier` sur l'identifiant de l'agrégat :

- `AddAccountCommand`
- `CreditAccountCommand`
- `DebitAccountCommand`
- `UpdateAccountStatusCommand`

### 7.2 Événements

- `AccountCreatedEvent`
- `AccountActivatedEvent`
- `AccountCreditedEvent`
- `AccountDebitedEvent`
- `AccountStatusUpdatedEvent`

Le payload des événements est sérialisé au format JSON avec Jackson.

### 7.3 Agrégat

`AccountAggregate` est annoté avec `@Aggregate`, son identifiant avec `@AggregateIdentifier`. Il contient :

- les **Command Handlers** (`@CommandHandler`) qui implémentent la fonction de décision ;
- les **Event Sourcing Handlers** (`@EventSourcingHandler`) qui implémentent la fonction d'évolution.

### 7.4 Modèle de lecture

- Entités JPA : `Account` et `AccountOperation`.
- Repositories Spring Data : `AccountRepository` et `AccountOperationRepository`.
- `AccountEventHandler` : projette les événements dans la base de lecture et alimente le `QueryUpdateEmitter`.
- `AccountQueryHandler` : traite `GetAllAccountsQuery` et `GetAccountStatementQuery`.

## 8. Prérequis

- JDK 21
- Maven 3.9 ou supérieur
- Docker et Docker Compose
- Un navigateur web

## 9. Installation et lancement

### 9.1 Cloner le dépôt

```bash
git clone https://github.com/fatima-aitoulahyan/es-cqrs-axon.git
cd es-cqrs-axon
```

### 9.2 Démarrer l'infrastructure

```bash
docker compose up -d
```

Services démarrés :

| Service | URL | Description |
|---------|-----|-------------|
| PostgreSQL | `localhost:5432` | Base `accounts_db` |
| PgAdmin | http://localhost:8088 | Administration PostgreSQL |
| Axon Server | http://localhost:8024 | Console d'administration (gRPC sur le port 8124) |

### 9.3 Lancer l'application

```bash
mvn spring-boot:run
```

L'application est accessible sur http://localhost:8080.

### 9.4 Accès aux interfaces

| Interface | URL |
|-----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Console Axon Server | http://localhost:8024 |
| PgAdmin | http://localhost:8088 |

### 9.5 Démarrage progressif (approche du TP)

Le TP procède par étapes :

1. **Event Store en mémoire** : la dépendance `axon-server-connector` est exclue dans le `pom.xml`, les événements sont stockés dans H2.
2. **Event Store PostgreSQL** : `application.properties` est configuré pour utiliser PostgreSQL et conserver l'historique.
3. **Axon Server** : l'exclusion est supprimée et le service Axon Server est ajouté au `docker-compose.yml`.

## 10. API REST

### Côté commande (`account-commande-controller`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/commands/accounts/add` | Créer un compte |
| POST | `/commands/accounts/credit` | Créditer un compte |
| POST | `/commands/accounts/debit` | Débiter un compte |
| POST | `/commands/accounts/updateStatus` | Modifier le statut d'un compte |
| GET | `/commands/accounts/events/{accountId}` | Consulter les événements d'un compte |

### Côté lecture (`account-query-controller`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/query/accounts/all` | Lister tous les comptes |
| GET | `/query/accounts/accountStatement{accountId}` | Relevé d'un compte |
| GET | `/query/accounts/watch/{accountId}` | Flux temps réel (SSE) des opérations |

### Exemples de corps de requête

**Création d'un compte**

```json
{
  "initialBalance": 2000,
  "currency": "MAD"
}
```

**Crédit ou débit**

```json
{
  "accountId": "070a8046-7b95-4e73-8397-6220023a0aaa",
  "amount": 200,
  "currency": "MAD"
}
```

**Changement de statut**

```json
{
  "accountId": "19dd4c46-0f89-4afa-bea3-dcd95b739f55",
  "accountStatus": "SUSPENDED"
}
```

## 11. Démonstration et résultats

### 11.1 Documentation Swagger

L'ensemble des endpoints est exposé et testable via Swagger UI.

![Endpoints exposés dans Swagger UI](docs/images/10.png)

### 11.2 Côté commande

#### Création d'un compte

La requête `POST /commands/accounts/add` retourne l'identifiant du compte créé.

![Création d'un compte](docs/images/img1.png)

#### Crédit d'un compte

![Crédit d'un compte](docs/images/img4.png)

#### Débit d'un compte

![Débit d'un compte](docs/images/5.png)

#### Modification du statut d'un compte

![Mise à jour du statut d'un compte](docs/images/img5.png)

### 11.3 Côté lecture

#### Liste des comptes

L'endpoint `GET /query/accounts/all` renvoie les comptes projetés dans la base de lecture.

![Liste de tous les comptes](docs/images/img7.png)

#### Relevé d'un compte

L'endpoint `GET /query/accounts/accountStatement{accountId}` renvoie le compte et l'historique de ses opérations.

![Relevé d'un compte](docs/images/img8.png)

#### Base de lecture (PostgreSQL)

La table `account` de la base de lecture, consultée avec PgAdmin.

![Table account dans PgAdmin](docs/images/img6.png)

## 12. Évolution de l'Event Store

### 12.1 Event Store en mémoire (H2)

Dans la première phase, les événements sont enregistrés dans la table `DOMAIN_EVENT_ENTRY` d'une base H2 en mémoire (`jdbc:h2:mem:account-db`). Les données sont perdues à chaque redémarrage.

![Table DOMAIN_EVENT_ENTRY dans la console H2](docs/images/img2.png)

### 12.2 Event Store PostgreSQL

Dans la deuxième phase, les événements sont persistés dans PostgreSQL, ce qui permet de conserver l'historique entre les redémarrages.

![Table domain_event_entry dans PostgreSQL](docs/images/img13.png)

### 12.3 Axon Server

Dans la phase finale, la gestion de l'Event Store, le dispatching des commandes, des événements et des requêtes sont délégués à **Axon Server**. Le microservice s'y connecte via gRPC et apparaît dans la vue d'ensemble de la console.

![Vue d'ensemble d'Axon Server](docs/images/img9.png)

L'onglet **Event Store > Search** permet de consulter les événements d'un agrégat dans l'ordre de leur séquence : `AccountCreatedEvent`, `AccountActivatedEvent`, `AccountDebitedEvent`, puis `AccountCreditedEvent`.

![Événements dans l'Event Store d'Axon Server](docs/images/img11.png)

> Axon Server s'exécute ici en mode non enregistré (accès d'essai limité à 12 heures). Un enregistrement sur platform.axoniq.io est nécessaire pour un usage prolongé.

## 13. Conclusion

Ce projet met en œuvre une architecture complète Event Sourcing et CQRS :

- un côté commande qui valide les règles métier et enregistre des événements immuables ;
- un côté lecture alimenté par projection des événements dans une base relationnelle ;
- un suivi temps réel basé sur SSE et Project Reactor ;
- une infrastructure distribuée reposant sur Axon Server et gRPC.

Cette approche offre une traçabilité complète des opérations, une séparation nette des responsabilités et une bonne capacité d'évolution vers une architecture microservices.

## 14. Auteur

**Fatima AIT OULAHYAN**
GitHub : [fatima-aitoulahyan](https://github.com/fatima-aitoulahyan)

Réalisé dans le cadre de l'Activité Pratique N°4, sous l'encadrement de Mohamed YOUSSFI.
