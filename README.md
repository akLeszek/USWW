# System Wspomagania Komunikacji Student-Dziekanat (USWW)

## Opis Projektu
System Wspomagania Komunikacji Student-Dziekanat (USWW) to zaawansowana aplikacja webowa przeznaczona do efektywnej komunikacji między studentami a pracownikami dziekanatu. Projekt ma na celu usprawnienie procesu składania wniosków, zgłaszania problemów oraz wymiany informacji w środowisku akademickim.

## Technologie

### Backend
- **Język programowania**: Java 17
- **Framework**: Spring Boot 3.2.4
- **Baza danych**: Microsoft SQL Server
- **Bezpieczeństwo**: 
  - Spring Security
  - JWT (JSON Web Token)
  - Argon2 szyfrowanie haseł

### Frontend
- **Język programowania**: TypeScript
- **Framework**: Angular 17
- **UI Components**: Bootstrap, ng-bootstrap
- **Architektura**: Komponenty standalone

### Kluczowe Biblioteki i Narzędzia
- **Backend**:
  - Hibernate JPA
  - Lombok
  - Jackson
  - Papaparse
  - Argon2 Password Encoder

- **Frontend**:
  - RxJS
  - Angular Router
  - Reactive Forms
  - HttpClient

## Struktura Projektu

```
Aplikacja/usww/
│
├── backend (src/main/java/adrianles/usww)
│   ├── api/                 # Kontrolery REST API
│   ├── config/              # Konfiguracje systemowe
│   ├── domain/              # Modele danych i repozytoria
│   │   ├── entity/          # Encje bazodanowe
│   │   └── repository/      # Interfejsy repozytoriów
│   ├── exception/           # Obsługa wyjątków
│   ├── security/            # Komponenty bezpieczeństwa
│   └── service/             # Logika biznesowa
│
├── frontend (frontend/)
│   ├── src/
│   │   ├── app/
│   │   │   ├── admin/       # Moduł administracyjny
│   │   │   ├── auth/        # Komponenty autoryzacji
│   │   │   ├── dashboard/   # Widok pulpitu nawigacyjnego
│   │   │   ├── shared/      # Współdzielone komponenty
│   │   │   └── tickets/     # Moduł zgłoszeń
│   │   ├── assets/
│   │   └── environments/
│
└── resources/
    ├── application.properties
    └── dbScripts/           # Skrypty bazodanowe
```

## Kluczowe Funkcjonalności
- Rejestracja i zarządzanie zgłoszeniami
- System uprawnień (Student, Operator, Administrator)
- Wielowarstwowe bezpieczeństwo
- Zarządzanie użytkownikami
- Kategoryzacja i priorytetyzacja zgłoszeń
- Obsługa załączników

## Uruchomienie Projektu

### Wymagania
- Java 17
- Node.js 22
- Angular CLI
- Microsoft SQL Server

### Kroki Instalacji
1. Sklonuj repozytorium
2. Skonfiguruj bazę danych
3. Ustaw zmienne środowiskowe
4. Uruchom backend: `./gradlew bootRun`
5. Uruchom frontend: `ng serve`

## Autor
Adrian Leś
Wyższa Szkoła Technologii Informatycznych w Katowicach
Kierunek: Informatyka
Rok akademicki: 2023/2024
