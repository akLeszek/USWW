-- Dane słownikowe dla USER_GROUP
INSERT INTO USER_GROUP (idn, name) VALUES
('ADMIN', 'Administratorzy'),
('TECH', 'Technicy'),
('STUDENT', 'Studenci');

-- Dane słownikowe dla ORGANIZATION_UNIT
INSERT INTO ORGANIZATION_UNIT (idn, name) VALUES
('IT', 'Dział IT'),
('DZIEKANAT', 'Dziekanat'),
('ADMIN', 'Administracja');

-- Dane słownikowe dla TICKET_CATEGORY
INSERT INTO TICKET_CATEGORY (idn, name) VALUES
('LOGOWANIE', 'Problemy z logowaniem'),
('KURS', 'Problemy z dostępem do kursów'),
('INNE', 'Inne zgłoszenia');

-- Dane słownikowe dla TICKET_STATUS
INSERT INTO TICKET_STATUS (idn, name) VALUES
('NOWE', 'Nowe'),
('W_TRAKCIE', 'W trakcie realizacji'),
('ZAMKNIETE', 'Zamknięte');

-- Dane słownikowe dla TICKET_PRIORITY
INSERT INTO TICKET_PRIORITY (idn, name) VALUES
('LOW', 'Niski'),
('MEDIUM', 'Średni'),
('HIGH', 'Wysoki'),
('CRITICAL', 'Krytyczny');

-- Użytkownicy testowi (hasło: "haslo1234" dla wszystkich)
INSERT INTO USERS (login, password, forename, surname, login_ban, group_id, organization_unit_id, archive, first_login) VALUES
('admin', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Admin', 'Testowy', FALSE, 1, 3, FALSE, FALSE),
('technician', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Technik', 'Testowy', FALSE, 2, 1, FALSE, FALSE),
('student', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Student', 'Testowy', FALSE, 3, 2, FALSE, FALSE);

-- Przykładowe zgłoszenie
INSERT INTO TICKET (title, operator_id, student_id, status_id, category_id, priority_id, inserted_date, archive) VALUES
('Testowe zgłoszenie', 2, 3, 1, 1, 2, CURRENT_TIMESTAMP(), FALSE);

-- Przykładowa wiadomość w zgłoszeniu
INSERT INTO TICKET_MESSAGE (ticket_id, sender_id, message_text, insert_date, archive) VALUES
(1, 3, 'To jest testowa wiadomość w zgłoszeniu', CURRENT_TIMESTAMP(), FALSE);
