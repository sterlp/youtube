Die Komponentenarchitektur ist ein Vorschlag wie man in Softwareprojekten framework neutral den Code effektiv strukturieren kann. Egal ob Java, C#, TypeSkript oder Dart. Unabhängig vom Framework wie Spring Boot, Quarkus, Micronaut, CDI, Angular oder React.

https://youtu.be/luaDzMKyF0g


# Domain Model & Architecture Rules

This document defines the domain model conventions, component architecture, and development practices for this Spring Boot application.

## Table of Contents

- [Component Architecture](#component-architecture)
- [Persistence Layer](#persistence-layer)
- [Database Conventions](#database-conventions)
- [API Layer](#api-layer)
- [General Development Rules](#general-development-rules)
- [Testing Guidelines](#testing-guidelines)

---

## Component Architecture

### TL;DR - Architecture Overview

Each component is isolated in its own package. **Access to a component MUST go through its Service classes (public API).**

**Golden Rule:** Components are encapsulated. Access ONLY through Service classes.

| Layer | Package | Suffix | Annotation | Access Rules |
|-------|---------|--------|------------|------------|
| Service | root package | `*Service` | `@Service` | Public API of component |
| Command | `command` | `*Command` | `@Command` | Private, called by Service |
| Repository | `repository` | `*Repository` | `@Repository` | Called by Service/Command in same module |
| Entity | `model` | `*Entity` | `@Entity` | Shared between components (read-only) |
| API Model | `api.<module>.model` | No suffix | - | External representation |
| Resource | `api` | `*Resource` | `@RestController` | REST endpoints, no business logic |
| Timer | root / `timer` | `*Timer` | `@Component` | Delegates to Service, no logic |

**Transaction Flow:** Service starts → Command participates (MANDATORY) → Repository operates in transaction

---

### Package Structure

```
de.netze.utilities.sap.us4g_adapter.person/
├── PersonService.java           # Public API (root package)
├── api/
│   ├── PersonResource.java      # REST endpoint
│   ├── model/
│   │   └── Person.java          # API class (no suffix)
│   └── converter/
│       └── PersonEntityToPersonConverter.java
├── command/
│   ├── CreatePersonCommand.java
│   └── DeletePersonCommand.java
├── model/
│   └── PersonEntity.java        # Entity (with suffix)
├── repository/
│   └── PersonRepository.java
└── timer/
    └── PersonCleanupTimer.java
```

All classes except Model/Entity classes and Converters are considered **private** to the component.

---

### Service Classes (e.g., `PersonService`)

**Location:** Root package of the component  
**Annotation:** `@Service` + `@Transactional(timeout=10_000)`  
**Responsibility:** High-level workflow orchestration - Public API of the component

**Characteristics:**
- Implements "Workflow" and high-level logic
- Manages cross-cutting concerns:
  - Transaction management (via @Transactional)
  - Authorization
  - Caching
  - Policies
- Method names are **generic** (e.g., `savePerson()`, `findPerson()`)
- Usually has NO private methods (delegate to Commands)
- **May call:**
  - Other Services (from other components)
  - Commands in the same module
  - Repositories in the same module

**JavaDoc:** Each Service MUST have a short description stating its responsibility clearly.

**Example:**
```java
/**
 * Service für Verwaltung von Personen.
 * Verantwortlich für alle Person-bezogenen Workflows.
 */
@Service
@Transactional(timeout = 10_000)
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final CreatePersonCommand createPersonCommand;
    
    public Person createPerson(String firstName, String lastName) {
        return createPersonCommand.call(firstName, lastName);
    }
}
```

---

### Command Classes (e.g., `DeletePersonCommand`)

**Location:** `command` sub-package  
**Annotation:** `@Command` (internally: `@Component` + `@Transactional(propagation=MANDATORY)`)  
**Responsibility:** Implements a single use-case or low-level logic step  
**Naming:** Named after the use-case with "Command" suffix (e.g., `DeletePersonCommand`, `ValidateEmailCommand`)

**Characteristics:**
- Usually has ONE main method: `call()`
- Method names can be **specific** (e.g., `call()`, `validateEmailFormat()`)
- Like a "private method" extracted from Service
- Must be testable in isolation
- Runs in MANDATORY transaction (requires existing transaction from Service)
- **May call:**
  - Other Commands in the same module
  - Foreign Services (for cross-module dependencies)
- **May NOT:**
  - Call its own Service (no upward delegation)
  - Call foreign Repositories directly

**Example:**
```java
@Command
@RequiredArgsConstructor
public class DeletePersonCommand {
    private final PersonRepository personRepository;
    
    public void call(Long personId) {
        PersonEntity person = personRepository.findById(personId)
            .orElseThrow(() -> new EntityNotFoundException("Person not found"));
        personRepository.delete(person);
    }
}
```

---

### Timer Classes (e.g., `PersonCleanupTimer`)

**Location:** Root package (with Service) or `timer` sub-package  
**Responsibility:** Scheduled tasks - delegates to Service  
**Annotation:** `@Scheduled`

**Rule:** Timer has NO business logic except logging. All logic MUST be in Service or Command.

**Example:**
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class PersonCleanupTimer {
    private final PersonService personService;
    
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupInactivePersons() {
        log.info("Starting person cleanup timer");
        personService.deleteInactivePersons();
        log.info("Person cleanup timer completed");
    }
}
```

---

## Persistence Layer

### Entities

**Location:** `model` package  
**Naming:** Suffix with `*Entity` (e.g., `PersonEntity`, `OrderEntity`)  
**Rationale:** Avoids name collision with API classes (which have no suffix)

```java
package de.netze.utilities.sap.us4g_adapter.model;

@Entity
@Table(name = "person")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

---

## Database Conventions

- **Table Names:** Lowercase for maximum DBMS compatibility (`person`, `order_item`)
- **Index Names:** Lowercase (`idx_person_email`)
- **Column Names:** Lowercase, snake_case for multi-word columns
- **Test Database:** H2 in-memory database
- **Hibernate Strategy:** `spring.jpa.hibernate.ddl-auto=create-drop` (for development/testing)

---

## Repositories

**Location:** `repository` package  
**Naming:** Suffix with `*Repository` (e.g., `PersonRepository`)  
**Access Control:** May ONLY be called by:
- Services in the same module
- Commands in the same module

**Example:**
```java
package de.netze.utilities.sap.us4g_adapter.person.repository;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    List<PersonEntity> findByLastName(String lastName);
}
```

**Rule:** Repositories implement the "Persistence" layer - abstraction of database queries.

---

## API Layer

### API Model Classes

**Location:** `api.<module>.model` package  
**Naming:** NO suffix (e.g., `Person`, `Order`)  
**Purpose:** External representation for REST APIs

```java
package de.netze.utilities.sap.us4g_adapter.person.api.model;

public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    // ...
}
```

### Entity → API Conversion

**Approach 1: Converter (Preferred for complex mappings)**
- Use Spring `Converter<S, T>` interface
- Location: `api.<module>.converter` package or directly in `api`together with the resource
- Converts `*Entity` → API class

```java
@Component
public class PersonEntityToPersonConverter implements Converter<PersonEntity, Person> {
    @Override
    public Person convert(PersonEntity entity) {
        Person person = new Person();
        person.setId(entity.getId());
        person.setFirstName(entity.getFirstName());
        // ...
        return person;
    }
}
```

**Approach 2: Repository Projections**
- Use Spring Data projections (interface-based)
- Only when direct mapping is sufficient

### REST Resources

**Location:** `api` package  
**Naming:** Suffix with `*Resource` (e.g., `PersonResource`)  
**Purpose:** Anti-corruption layer - implements public REST API
**Responsibility:** Delegates to Service, NO business logic

```java
@RestController
@RequestMapping("/api/persons")
public class PersonResource {
    private final PersonService personService;
    
    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        return personService.findPerson(id); // Service returns API class
    }
}
```

---

## General Development Rules

### Code Quality

- **Clean Code principles** apply
- **Uncle Bob's SOLID principles** should be considered where useful
- **4+ Parameters Rule:** If a method has more than 4 parameters, consider a Value/Entity class
- **Zalando REST API Guidelines** apply for all REST endpoints
- **Pre-Commit:** Run `mvn clean install` before every commit / end of work

### Method Complexity

If a method exceeds 4 parameters:
```java
// ❌ Avoid
public void createPerson(String firstName, String lastName, String email, String phone, String address);

// ✅ Better
public void createPerson(PersonData personData);
```

---

## Testing Guidelines

### Test Structure

Each major functionality should be tested. One test class per production class.

**Test Subject:** Use a variable named `subject` to make clear which component is being tested.

**Test Pattern:** Structure EVERY test with GIVEN/WHEN/THEN comments:

```java
@Test
void shouldCreatePersonSuccessfully() {
    // GIVEN: Setup code and test data
    String firstName = "Max";
    String lastName = "Mustermann";
    
    // WHEN: Action / modification code calling the service/command
    Person result = subject.createPerson(firstName, lastName);
    
    // THEN: Assert code - verify expected outcome
    assertThat(result).isNotNull();
    assertThat(result.getFirstName()).isEqualTo(firstName);
    assertThat(result.getLastName()).isEqualTo(lastName);
}
```

### Random Test Data

Tests should work with **random data** where possible, so explicit cleanup (`@BeforeEach` clear) is generally not required.

**Example:**
```java
@Test
void shouldFindPersonByEmail() {
    // GIVEN: Random test data
    String email = "test-" + UUID.randomUUID() + "@example.com";
    PersonEntity person = createRandomPerson(email);
    personRepository.save(person);
    
    // WHEN
    Optional<PersonEntity> result = subject.findByEmail(email);
    
    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(email);
}
```

### Test Database

- Use H2 in-memory database for tests
- Configure in `src/test/resources/application.yml`:
  ```yaml
  spring:
    datasource:
      url: jdbc:h2:mem:testdb
      driver-class-name: org.h2.Driver
    jpa:
      hibernate:
        ddl-auto: create-drop
      show-sql: true
  ```

---

## Summary

See [Component Architecture Overview](#component-architecture) for the complete architecture table.

### Custom Annotations

**@Command**
```java
@Transactional(propagation = Propagation.MANDATORY)
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {}
```
Combines `@Component` with MANDATORY transaction propagation (requires existing transaction).

**Alternative: @Service**  
If you prefer a custom annotation instead of plain `@Service + @Transactional`, you can use:
```java
@Transactional(timeout = 10_000)
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {}
```
Combines `@Service` with transaction management (10-second timeout).
