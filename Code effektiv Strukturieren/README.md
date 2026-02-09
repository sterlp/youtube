Die Komponentenarchitektur ist ein Vorschlag wie man in Softwareprojekten framework neutral den Code effektiv strukturieren kann. Egal ob Java, C#, TypeSkript oder Dart. Unabhängig vom Framework wie Spring Boot, Quarkus, Micronaut, CDI, Angular oder React.

https://youtu.be/luaDzMKyF0g


# Domain Model & Architecture Rules

Domain conventions, component architecture, development practices.

## Table of Contents

- [Component Architecture](#component-architecture)
- [Persistence Layer](#persistence-layer)
- [Database Conventions](#database-conventions)
- [API Layer](#api-layer)
- [General Development Rules](#general-development-rules)
- [Testing Guidelines](#testing-guidelines)

---

## Component Architecture

### TL;DR

Components isolated in packages. **Access ONLY through Service classes.**

**Golden Rule:** Service = public API. Everything else = private.

| Layer | Package | Suffix | Annotation | Access Rules |
|-------|---------|--------|------------|------------|
| Service | root | `*Service` | `@Service` | Public API |
| Command | `command` | `*Command` | `@Command` | Private, called by Service |
| Repository | `repository` | `*Repository` | `@Repository` | Called by Service/Command in same module |
| Entity | `model` | `*Entity` | `@Entity` | Shared across components (read-only) |
| API Model | `api.<module>.model` | No suffix | - | External representation |
| Resource | `api` | `*Resource` | `@RestController` | Delegates to Service, converts Entity↔API |
| Timer | root / `timer` | `*Timer` | `@Component` | Delegates to Service |

**Flow:** Service starts → Command participates (TX) → Repository operates  
**Conversion:** Service returns Entity → Resource converts → API model

---

## Custom Annotations

### @Command

```java
@Transactional(propagation = Propagation.MANDATORY)
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {}
```

**Why MANDATORY?**  
Command needs transaction from Service. Fails fast if called alone.
Think: "private method" extracted from Service.

---

## Transaction Management

### Service TX

`@Transactional(timeout = 10_000)` - starts transaction, 10s timeout

Use `@Transactional(readOnly = true)` for queries - Hibernate optimizes flush.

```java
@Service
@Transactional(timeout = 10_000)  // writes
public class PersonService { }

@Service
@Transactional(readOnly = true)   // reads
public class PersonQueryService { }
```

### Command TX

`MANDATORY` = needs existing transaction. Throws `TransactionRequiredException` if none.
```java
// ❌ This will FAIL at runtime
@Component
public class SomeInvalidCaller {
    private final CreatePersonCommand command;
    
    public void doSomething() {
        command.call("John", "Doe"); // TransactionRequiredException!
    }
}

// ✅ Correct - called from Service with transaction
@Service
@Transactional(timeout = 10_000)
public class PersonService {
    private final CreatePersonCommand command;
    
    public PersonEntity createPerson(String firstName, String lastName) {
        return command.call(firstName, lastName); // Works - transaction exists
    }
}
```

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

### Service Classes

**Package:** root  
**Annotation:** `@Service + @Transactional(timeout=10_000)`  
**Role:** Public API, workflow orchestration

**Characteristics:**
- Workflow + high-level logic
- Cross-cutting: TX, auth, caching, policies
- Generic method names (`savePerson()`, `findPerson()`)
- No private methods → extract to Command (except trivial helpers)
- Returns Entities (not API models)

**May call:** Services (other components), Commands, Repositories  
**JavaDoc:** Short responsibility statement required

**Example:**
```java
@Service
@Transactional(timeout = 10_000)
@RequiredArgsConstructor
public class PersonService {
    private final CreatePersonCommand createPersonCommand;
    
    public PersonEntity createPerson(String firstName, String lastName) {
        return createPersonCommand.call(firstName, lastName);
    }
}
```

**Direct Repository call** (simple ops):
```java
@Service
@Transactional(timeout = 10_000)
public class PersonService {
    public PersonEntity findPerson(Long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));
    }
}
```

**When to delegate vs inline:**
- **Use Command:** Logic >10 lines, reused, needs testing, complex rules
- **Inline:** Simple CRUD, single-line, no logic

---

### Command Classes

**Package:** `command`  
**Annotation:** `@Command` = `@Component + MANDATORY TX`  
**Role:** Single use-case, low-level logic

**Characteristics:**
- ONE method: `call()`
- Specific method names OK (`validateEmailFormat()`)
- Testable in isolation
- Needs existing TX from Service

**May call:** Commands (same module), Services (other components)  
**May NOT:** Own Service (no upward), foreign Repositories

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

### Timer Classes

**Package:** root or `timer`  
**Annotation:** `@Scheduled`  
**Rule:** No logic, only logging. Delegate to Service.

**Example:**
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class PersonCleanupTimer {
    private final PersonService personService;
    
    @Scheduled(cron = "0 0 2 * * ?\")  // 2 AM daily
    public void cleanupInactivePersons() {
        log.info("Starting cleanup");
        personService.deleteInactivePersons();
        log.info("Cleanup done");
    }
}
```

---

## Persistence Layer

### Entities

**Package:** `model`  
**Naming:** `*Entity` suffix (avoids collision with API classes)

```java
@Entity
@Table(name = "person")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

---

## Database Conventions

- **Tables:** Lowercase (`person`, `order_item`)
- **Indexes:** Lowercase (`idx_person_email`)
- **Columns:** Lowercase, snake_case
- **Test DB:** H2 in-memory
- **Hibernate:** `create-drop` (dev/test)

---

## Repositories

**Package:** `repository`  
**Access:** Service/Command in same module only

```java
@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    List<PersonEntity> findByLastName(String lastName);
}
```

Role: Persistence abstraction

---

## API Layer

### API Models

**Package:** `api.<module>.model`  
**Naming:** NO suffix  
**Purpose:** External REST representation

```java
public class Person {
    private Long id;
    private String firstName;
    private String lastName;
}
```

### Entity → API Conversion

**Option 1: Converter** (complex mappings)

```java
@Component
public class PersonEntityToPersonConverter implements Converter<PersonEntity, Person> {
    public Person convert(PersonEntity entity) {
        return new Person(entity.getId(), entity.getFirstName(), entity.getLastName());
    }
}
```

**Location:**
- `api/converter/` - reused by multiple Resources
- `api/` - single Resource only
- Never in Service/Command

**Option 2: Projections** (simple mappings)

Spring Data interface projections - when 1:1 mapping.

### REST Resources

**Package:** `api`  
**Naming:** `*Resource` suffix  
**Role:** Anti-corruption layer - public REST API  
**Responsibility:** Delegate to Service, convert Entity↔API, no logic

```java
@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonResource {
    private final PersonService personService;
    private final Converter<PersonEntity, Person> entityToApiConverter;
    
    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        PersonEntity entity = personService.findPerson(id); // Service returns Entity
        return entityToApiConverter.convert(entity); // Resource converts to API
    }
}
```

---

## General Rules

- Clean Code + SOLID where useful
- **>4 params:** Use Value/Entity class
- **REST:** Zalando API Guidelines
- **Pre-commit:** `mvn clean install`

```java
// ❌
public void create(String a, String b, String c, String d, String e);

// ✅
public void create(PersonData data);
```

---

## Testing

One test class per production class.

**Subject:** Variable named `subject`  
**Pattern:** GIVEN/WHEN/THEN comments

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

**Random data:** No `@BeforeEach` cleanup needed
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

**Test DB:** H2 in-memory (`testdb`)

---

## Summary

[Component Architecture](#component-architecture) - full table

**Key Decisions:**
1. `@Service + @Transactional(timeout=10_000)` - explicit
2. Service returns Entity, Resource converts
3. Service starts TX (10s), Command participates (MANDATORY)
4. Extract to Command: >10 lines, reused, testable

