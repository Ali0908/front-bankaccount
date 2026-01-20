# Architecture Hexagonale - Bank Account Backend

## Vue d'ensemble

Ce projet suit désormais une **architecture hexagonale** (aussi appelée Ports & Adapters), qui sépare clairement :
- Le **domaine métier** (logique business pure)
- La **couche application** (orchestration des use cases)
- Les **adapters** (infrastructure technique)

## Structure du projet

```
src/main/java/com/bankaccount/back_bankaccount/
│
├── domain/                          # CŒUR MÉTIER (aucune dépendance externe)
│   ├── model/                       # Entités de domaine (POJO purs)
│   │   ├── BankAccount.java        # Logique métier des comptes
│   │   ├── Transaction.java        # Logique métier des transactions
│   │   ├── Statement.java          # Agrégat pour les relevés
│   │   └── TransactionType.java    # Enum du domaine
│   │
│   └── ports/                       # Interfaces (contrats)
│       ├── in/                      # Ports primaires (use cases)
│       │   ├── GetAllAccountsUseCase.java
│       │   ├── DepositMoneyUseCase.java
│       │   ├── WithdrawMoneyUseCase.java
│       │   ├── SetOverdraftLimitUseCase.java
│       │   ├── DepositToSavingsUseCase.java
│       │   └── GetStatementUseCase.java
│       │
│       └── out/                     # Ports secondaires (persistence, etc.)
│           ├── BankAccountRepositoryPort.java
│           └── TransactionRepositoryPort.java
│
├── application/                     # COUCHE APPLICATION
│   └── service/
│       └── BankAccountService.java  # Implémentation des use cases
│
├── adapters/                        # ADAPTERS (infrastructure)
│   ├── in/                          # Adapters primaires (entrée)
│   │   └── rest/                    # API REST
│   │       ├── BankAccountRestController.java
│   │       └── mapper/
│   │           ├── BankAccountDtoMapper.java
│   │           └── StatementDtoMapper.java
│   │
│   └── out/                         # Adapters secondaires (sortie)
│       └── persistence/             # Persistence JPA
│           ├── BankAccountPersistenceAdapter.java
│           ├── TransactionPersistenceAdapter.java
│           ├── entity/              # Entités JPA (infrastructure)
│           │   ├── BankAccountJpaEntity.java
│           │   ├── TransactionJpaEntity.java
│           │   └── TransactionType.java
│           ├── repository/          # Spring Data JPA repositories
│           │   ├── BankAccountJpaRepository.java
│           │   └── TransactionJpaRepository.java
│           └── mapper/
│               ├── BankAccountJpaMapper.java
│               └── TransactionJpaMapper.java
│
├── config/                          # Configuration Spring
│   └── HexagonalArchitectureConfig.java
│
├── dto/                             # DTOs (partagés)
├── exception/                       # Exceptions métier
└── BackBankaccountApplication.java  # Point d'entrée
```

## Principes de l'architecture hexagonale

### 1. **Domaine au centre**
- **Pas de dépendances externes** (pas de Spring, JPA, etc.)
- Contient toute la **logique métier**
- Les entités du domaine sont des **POJO purs**
- Exemple : `BankAccount.java` contient les règles métier (retrait, dépôt, découvert)

### 2. **Ports (Interfaces)**

#### Ports IN (primaires) - Use Cases
- Définissent **ce que l'application peut faire**
- Appelés par les adapters primaires (REST, CLI, etc.)
- Exemples : `DepositMoneyUseCase`, `WithdrawMoneyUseCase`

#### Ports OUT (secondaires) - Interfaces techniques
- Définissent **ce dont l'application a besoin**
- Implémentés par les adapters secondaires (BDD, API externes, etc.)
- Exemples : `BankAccountRepositoryPort`, `TransactionRepositoryPort`

### 3. **Application Layer**
- **Orchestre** les objets du domaine
- Implémente les **use cases** (ports IN)
- Utilise les **ports OUT** pour la persistence
- `BankAccountService` : implémente tous les use cases

### 4. **Adapters**

#### Adapters IN (primaires)
- **Point d'entrée** de l'application
- `BankAccountRestController` : expose l'API REST
- Convertit les DTOs → Domaine → DTOs

#### Adapters OUT (secondaires)
- **Point de sortie** vers l'infrastructure
- `BankAccountPersistenceAdapter` : implémente `BankAccountRepositoryPort`
- Convertit Domaine → Entités JPA → Domaine

## Flux de données

```
REST Request
    ↓
[REST Controller] (Adapter IN)
    ↓ (DTO → Domain)
[Use Case] (Port IN)
    ↓
[Application Service]
    ↓ (utilise Port OUT)
[Repository Port] (Port OUT)
    ↓
[Persistence Adapter] (Adapter OUT)
    ↓ (Domain → JPA Entity)
[JPA Repository]
    ↓
Base de données
```

## Avantages de cette architecture

### ✅ **Indépendance du domaine**
- La logique métier ne dépend d'aucune technologie
- Facile à tester (pas besoin de Spring, BDD, etc.)

### ✅ **Inversion de dépendances**
- Le domaine ne connaît pas l'infrastructure
- L'infrastructure dépend du domaine (via les ports)

### ✅ **Testabilité**
- Tests unitaires du domaine sans infrastructure
- Tests d'intégration via les adapters
- Mock facile des ports

### ✅ **Évolutivité**
- Changer de BDD : créer un nouvel adapter OUT
- Ajouter une API GraphQL : créer un nouvel adapter IN
- Pas besoin de toucher au domaine !

### ✅ **Maintenabilité**
- Séparation claire des responsabilités
- Code découplé et modulaire

## Exemples de code

### Entité de domaine (pure)
```java
@Data
@Builder
public class BankAccount {
    private Long id;
    private String accountNumber;
    private Double balance;
    
    // Logique métier pure
    public boolean canWithdraw(Double amount) {
        return (balance - amount) >= -overdraftLimit;
    }
}
```

### Port IN (Use Case)
```java
public interface DepositMoneyUseCase {
    BankAccount deposit(String accountNumber, Double amount);
}
```

### Port OUT (Repository)
```java
public interface BankAccountRepositoryPort {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    BankAccount save(BankAccount account);
}
```

### Application Service
```java
@Service
public class BankAccountService implements DepositMoneyUseCase {
    private final BankAccountRepositoryPort repository;
    
    public BankAccount deposit(String accountNumber, Double amount) {
        BankAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(...);
        account.deposit(amount);  // Logique domaine
        return repository.save(account);
    }
}
```

### Adapter IN (REST)
```java
@RestController
public class BankAccountRestController {
    private final DepositMoneyUseCase depositUseCase;
    
    @PostMapping("/deposit")
    public BankAccountDto deposit(@RequestBody DepositRequestDto dto) {
        BankAccount account = depositUseCase.deposit(dto.getAccountNumber(), dto.getAmount());
        return mapper.toDto(account);
    }
}
```

### Adapter OUT (Persistence)
```java
@Component
public class BankAccountPersistenceAdapter implements BankAccountRepositoryPort {
    private final BankAccountJpaRepository jpaRepository;
    
    public BankAccount save(BankAccount account) {
        BankAccountJpaEntity entity = mapper.toEntity(account);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
```

## Migration depuis l'ancienne architecture

L'ancienne structure (Controller → Service → Repository) a été refactorisée :

| Ancien | Nouveau |
|--------|---------|
| `model/BankAccountEntity` (JPA) | `domain/model/BankAccount` (POJO pur) + `adapters/out/persistence/entity/BankAccountJpaEntity` |
| `service/IBankAccountService` | Divisé en use cases dans `domain/ports/in/` |
| `service/BankAccountServiceImplementation` | `application/service/BankAccountService` |
| `repository/IBankAccountRepository` (JPA) | `domain/ports/out/BankAccountRepositoryPort` + `adapters/out/persistence/BankAccountPersistenceAdapter` |
| `controller/BankAccountController` | `adapters/in/rest/BankAccountRestController` |

## Tests

### Tests unitaires (domaine)
```java
// Pas besoin de Spring !
BankAccount account = new BankAccount();
account.setBalance(100.0);
account.setOverdraftLimit(50.0);

assertTrue(account.canWithdraw(120.0));
assertFalse(account.canWithdraw(200.0));
```

### Tests d'intégration (use cases)
```java
// Mock du port
@Mock
BankAccountRepositoryPort repository;

@Test
void shouldDepositMoney() {
    when(repository.findByAccountNumber("ACC-001"))
        .thenReturn(Optional.of(account));
    
    BankAccount result = depositUseCase.deposit("ACC-001", 50.0);
    
    assertEquals(150.0, result.getBalance());
}
```

## Conclusion

Cette architecture hexagonale garantit :
- **Séparation claire** entre métier et technique
- **Code testable** et maintenable
- **Évolutivité** sans impact sur le domaine
- **Conformité** aux principes SOLID et Clean Architecture
