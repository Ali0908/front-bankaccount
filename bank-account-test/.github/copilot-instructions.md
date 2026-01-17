🏦 Bank Account – AI Assistant Instructions
Project Overview

Ce projet est une application bancaire simulée développée avec Spring Boot 25 pour le backend et Angular 17 + Angular Material pour le frontend.
L’objectif est de mettre en place un système de gestion de comptes bancaires, avec :

Comptes courants et livrets d’épargne

Dépôts et retraits avec règles métier (découvert autorisé, plafond livret)

Relevés de compte mensuels avec liste des opérations

Le projet suit une approche TDD (Test Driven Development) : chaque feature est d’abord testée, puis implémentée.

🧱 Backend Architecture
Structure
backend/
│
├── src/main/java/com/example/bankaccount/
│   ├── model/               # Entités / modèles
│   │   ├── Account.java
│   │   ├── SavingsAccount.java
│   │   └── Transaction.java
│   │
│   ├── repository/          # Repositories Spring Data JPA
│   │   ├── AccountRepository.java
│   │   └── TransactionRepository.java
│   │
│   ├── service/             # Services métier
│   │   ├── AccountService.java
│   │   └── TransactionService.java
│   │
│   ├── mapper/              # DTO ↔ Entity (si nécessaire)
│   │   └── AccountMapper.java
│   │
│   └── controller/          # Endpoints REST
│       ├── AccountController.java
│       └── TransactionController.java
│
├── src/test/java/com/example/bankaccount/
│   ├── service/             # Tests unitaires des services
│   └── controller/          # Tests d’intégration des endpoints

Tech Stack Backend

Java 25

Spring Boot

Spring Web (REST)

Spring Data JPA

H2 Database (en mémoire pour tests et développement)

Spring Boot Test (JUnit 5 + Mockito)

🧪 Backend – TDD Workflow

Écrire les tests : vérifier le comportement attendu avant de coder.

Développer la feature : implémenter uniquement ce qui est nécessaire pour que les tests passent.

Refactorer si nécessaire : améliorer la lisibilité, la structure et la performance sans casser les tests.

Exemple : retrait sur compte courant avec découvert

@Test
void shouldNotAllowWithdrawalAboveBalanceWithoutOverdraft() {
    Account account = new CurrentAccount("ACC123", BigDecimal.valueOf(100), BigDecimal.ZERO);
    assertThrows(IllegalArgumentException.class, () -> account.withdraw(BigDecimal.valueOf(150)));
}

🏗 Frontend Architecture
Structure avec features/ (colocation de code)
frontend/src/app/
│
├── features/
│   ├── accounts/
│   │   ├── list/
│   │   │   ├── list.component.ts
│   │   │   ├── list.component.html
│   │   │   ├── list.component.scss
│   │   │   └── list.service.ts
│   │   └── detail/
│   │       ├── detail.component.ts
│   │       ├── detail.component.html
│   │       ├── detail.component.scss
│   │       └── detail.service.ts
│   │
│   ├── operations/
│   │   ├── deposit/
│   │   │   ├── deposit.component.ts
│   │   │   ├── deposit.component.html
│   │   │   ├── deposit.component.scss
│   │   │   └── deposit.service.ts
│   │   └── withdrawal/
│   │       ├── withdrawal.component.ts
│   │       ├── withdrawal.component.html
│   │       ├── withdrawal.component.scss
│   │       └── withdrawal.service.ts
│   │
│   └── statements/
│       ├── statement-list/
│       │   ├── statement-list.component.ts
│       │   ├── statement-list.component.html
│       │   └── statement-list.service.ts
│       └── statement-detail/
│           ├── statement-detail.component.ts
│           ├── statement-detail.component.html
│           └── statement-detail.service.ts
│
├── shared/                    # Composants réutilisables et utilitaires
│   ├── components/            # Boutons, tables, modals génériques
│   ├── utils/                 # Fonctions utilitaires
│   └── pipes/                 # Pipes réutilisables
│
├── app.module.ts               # Module principal
├── app.routing.ts              # Routes globales
└── app.config.ts               # Intercepteurs, guards, providers

Tech Stack Frontend

Angular 17

Angular Material (UI, tables, boutons, formulaires)

RxJS / Signals pour la gestion d’état simple (pas de NgRx)

SCSS pour le style

Structure colocalisée par feature pour une meilleure DX

🧪 Frontend – TDD Workflow

Écrire d’abord les tests unitaires pour les composants et services (Jest / Angular Testing Library).

Développer la feature pour passer les tests.

Refactorer les composants si nécessaire.

Tester les intégrations avec HTTPClient et les endpoints backend.

Exemple : dépôt sur un compte courant

it('should deposit amount if under plafond', () => {
  const service = TestBed.inject(DepositService);
  service.deposit(accountId, 500).subscribe(result => {
    expect(result.balance).toBe(1500);
  });
});

🚀 Workflow Global

Commencer par les tests backend pour chaque feature (Account, Transaction).

Implémenter les services backend pour passer les tests.

Développer les endpoints REST et tester les contrôleurs.

Passer au frontend : créer les composants et services dans features/.

Écrire les tests frontend, puis implémenter les composants pour les faire passer.

Refactorer, améliorer UI/UX et valider intégration complète backend ↔ frontend.

📌 Bonnes pratiques

TDD : tests avant le code → sécurité et robustesse

Colocation de code : composants, services et routes/features au même niveau

Shared : ne mettre que ce qui est réutilisable dans plusieurs features

Separation of concerns : chaque feature gère son domaine métier

DX (Developer Experience) : code clair et facile à comprendre pour un nouveau dev

Pas de NgRx inutile : utiliser Signals / BehaviorSubject pour l’état