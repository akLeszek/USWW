-- Dane słownikowe dla USER_GROUP
INSERT INTO USER_GROUP (idn, name) VALUES
('ADMIN', 'Administratorzy'),
('OPERATOR', 'Operatorzy'),
('STUDENT', 'Studenci');

-- Dane słownikowe dla ORGANIZATION_UNIT
INSERT INTO ORGANIZATION_UNIT (idn, name) VALUES
('WH', 'Wydział Humanistyczny'),
('WNP', 'Wydział Nauk Przyrodniczych'),
('WNS', 'Wydział Nauk Społecznych'),
('WNST', 'Wydział Nauk Ścisłych i Technicznych'),
('WPA', 'Wydział Prawa i Administracji'),
('WSNE', 'Wydział Sztuki i Nauk o Edukacji'),
('WT', 'Wydział Teologiczny');

-- Dane słownikowe dla TICKET_CATEGORY
INSERT INTO TICKET_CATEGORY (idn, name) VALUES
('LOGOWANIE', 'Problemy z logowaniem'),
('KURS', 'Problemy z dostępem do kursów'),
('INNE', 'Inne zgłoszenia');

-- Dane słownikowe dla TICKET_STATUS
INSERT INTO TICKET_STATUS (idn, name) VALUES
('NEW', 'Nowe'),
('IN_PROGRESS', 'W trakcie realizacji'),
('CLOSED', 'Zamknięte');

-- Dane słownikowe dla TICKET_PRIORITY
INSERT INTO TICKET_PRIORITY (idn, name) VALUES
('LOW', 'Niski'),
('MEDIUM', 'Średni'),
('HIGH', 'Wysoki'),
('CRITICAL', 'Krytyczny');

-- Użytkownicy testowi (hasło: "haslo1234" dla wszystkich)
INSERT INTO USERS (login, password, forename, surname, login_ban, last_login, group_id, organization_unit_id, archive, first_login) VALUES
('admin', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Jan', 'Kowalski', 0, CONVERT(DATETIME, '2024-02-16 10:00:00', 120), 1, 3, 0, 0),
('unknown_operator', '', 'Nieokreślony', 'Operator', 1, null, 2, null, 0, 0),
('technician1', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Piotr', 'Nowak', 0, CONVERT(DATETIME, '2024-02-16 10:10:00', 120), 2, 1, 0, 1),
('student1', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Anna', 'Zielińska', 0, CONVERT(DATETIME, '2024-02-16 10:15:00', 120), 3, 2, 0, 1);
