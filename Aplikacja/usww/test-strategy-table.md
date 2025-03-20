# Rodzaje testów dla poszczególnych elementów systemu

## Backend (Java/Spring Boot)

| Element systemu | Rodzaj testów | Narzędzia | Co testujemy |
|----------------|---------------|-----------|--------------|
| **Entities** | Testy jednostkowe | JUnit 5, AssertJ, Bean Validation Tester | Walidację pól, konstruktory, metody toString/equals/hashCode |
| **Entities** | Testy integracyjne | Spring Data JPA Test, H2 | Mapowanie do bazy danych, relacje między tabelami, kaskadowość operacji |
| **Controllers** | Testy jednostkowe | JUnit 5, Mockito, MockMvc | Poprawność mapowania URL, status HTTP, serializację JSON |
| **Controllers** | Testy integracyjne | Spring Boot Test, TestRestTemplate | Integrację z pozostałymi komponentami, obsługę autoryzacji |
| **Services** | Testy jednostkowe | JUnit 5, Mockito | Logikę biznesową, obsługę wyjątków |
| **Services** | Testy integracyjne | Spring Boot Test | Integrację z repozytoriami, transakcyjność |
| **Repositories** | Testy integracyjne | Spring Data Test, H2 | Poprawność zapytań, operacje CRUD |
| **Mappers** | Testy jednostkowe | JUnit 5, AssertJ | Poprawną konwersję między Entity a DTO |
| **Security (JWT)** | Testy jednostkowe | JUnit 5, jjwt | Generowanie i walidację tokenów |
| **Security (Filter)** | Testy integracyjne | Spring Security Test | Filtrowanie żądań, autoryzację |
| **ExceptionHandlers** | Testy jednostkowe | JUnit 5, MockMvc | Mapowanie wyjątków na odpowiedzi HTTP |
| **Specifications** | Testy jednostkowe | JUnit 5, AssertJ | Poprawność kryteriów filtrowania |
| **Database Schema** | Testy migracji | Flyway, TestContainers | Poprawność skryptów migracyjnych |
| **Ticket Flow** | Testy przepływów | Spring Boot Test | End-to-end przepływ obsługi zgłoszeń |
| **User Management** | Testy przepływów | Spring Boot Test | End-to-end przepływ zarządzania użytkownikami |

## Frontend (Angular)

| Element systemu | Rodzaj testów | Narzędzia | Co testujemy |
|----------------|---------------|-----------|--------------|
| **Components** | Testy jednostkowe | Jasmine, Karma | Inicjalizację, renderowanie, obsługę zdarzeń |
| **Services** | Testy jednostkowe | Jasmine, Karma | Komunikację z API, przetwarzanie danych |
| **Guards** | Testy jednostkowe | Jasmine, Karma | Logikę kontroli dostępu |
| **Interceptors** | Testy jednostkowe | Jasmine, Karma | Modyfikację nagłówków HTTP, obsługę tokenów |
| **Forms** | Testy jednostkowe | Jasmine, Karma | Walidację formularzy, obsługę błędów |
| **Routing** | Testy jednostkowe | Jasmine, Karma | Poprawność routingu, przekierowania |
| **UI Integration** | Testy integracyjne | Angular Testing Library | Interakcje między komponentami |
| **E2E Flows** | Testy end-to-end | Cypress/Protractor | Przepływy użytkownika, pełne scenariusze |

## Szczegółowa strategia dla kluczowych elementów

| Komponent | Priorytet | Szczegółowe przypadki testowe |
|----------|-----------|------------------------------|
| **TicketServiceImpl** | Wysoki | - Tworzenie nowych zgłoszeń<br>- Aktualizacja zgłoszeń<br>- Filtrowanie zgłoszeń<br>- Archiwizacja zgłoszeń |
| **JwtUtil** | Wysoki | - Generowanie tokenów<br>- Walidacja tokenów<br>- Obsługa wygasłych tokenów |
| **UserStatusServiceImpl** | Wysoki | - Blokowanie/odblokowywanie użytkowników<br>- Archiwizacja użytkownika i jego zgłoszeń<br>- Przywracanie użytkowników |
| **AuthController** | Wysoki | - Logowanie<br>- Zmiana hasła<br>- Obsługa pierwszego logowania |
| **TicketSpecifications** | Średni | - Poprawność kryteriów filtrowania<br>- Złożone warunki wyszukiwania |
| **Login Component** | Wysoki | - Walidacja formularza<br>- Obsługa błędów<br>- Przekierowania |
