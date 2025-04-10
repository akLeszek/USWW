INSERT INTO USER_GROUP (idn, name) VALUES
('ADMIN', 'Administratorzy'),
('OPERATOR', 'Operatorzy'),
('STUDENT', 'Studenci');

INSERT INTO ORGANIZATION_UNIT (idn, name) VALUES
('WH', 'Wydział Humanistyczny'),
('WNP', 'Wydział Nauk Przyrodniczych'),
('WNS', 'Wydział Nauk Społecznych'),
('WNST', 'Wydział Nauk Ścisłych i Technicznych'),
('WPA', 'Wydział Prawa i Administracji'),
('WSNE', 'Wydział Sztuki i Nauk o Edukacji'),
('WT', 'Wydział Teologiczny');

INSERT INTO TICKET_CATEGORY (idn, name) VALUES
('LOGOWANIE', 'Problemy z logowaniem'),
('KURS', 'Problemy z dostępem do kursów'),
('INNE', 'Inne zgłoszenia');

INSERT INTO TICKET_STATUS (idn, name) VALUES
('NEW', 'Nowe'),
('IN_PROGRESS', 'W trakcie realizacji'),
('CLOSED', 'Zamknięte');

INSERT INTO TICKET_PRIORITY (idn, name) VALUES
('LOW', 'Niski'),
('MEDIUM', 'Średni'),
('HIGH', 'Wysoki'),
('CRITICAL', 'Krytyczny');

-- Dodanie kolumny first_login dla użytkowników
INSERT INTO USERS (login, password, forename, surname, login_ban, last_login, group_id, organization_unit_id, archive, first_login) VALUES
('admin', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Jan', 'Kowalski', 0, CONVERT(DATETIME, '2024-02-16 10:00:00', 120), 1, 3, 0, 0),
('unknown_operator', '', 'Nieokreślony', 'Operator', 1, null, 2, null, 0, 0),
('technician1', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Piotr', 'Nowak', 0, CONVERT(DATETIME, '2024-02-16 10:10:00', 120), 2, 1, 0, 1),
('student1', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Anna', 'Zielińska', 0, CONVERT(DATETIME, '2024-02-16 10:15:00', 120), 3, 2, 0, 1);

INSERT INTO TICKET (operator_id, student_id, status_id, category_id, priority_id, inserted_date, change_date, archive, title, last_activity_date) VALUES
(2, 3, 1, 1, 2, CONVERT(DATETIME, '2024-02-16 11:00:00', 120), NULL, 0, 'Nie mogę się zalogować', CONVERT(DATETIME, '2024-02-16 11:00:00', 120)),
(2, 3, 2, 2, 2, CONVERT(DATETIME, '2024-02-16 11:30:00', 120), CONVERT(DATETIME, '2024-02-16 12:00:00', 120), 0, 'Brak dostępu do kursu', CONVERT(DATETIME, '2024-02-16 12:00:00', 120)),
(2, 3, 3, 3, 2, CONVERT(DATETIME, '2024-02-15 09:00:00', 120), CONVERT(DATETIME, '2024-02-16 13:00:00', 120), 1, 'Problem z aplikacją', CONVERT(DATETIME, '2024-02-16 13:00:00', 120));

INSERT INTO TICKET_MESSAGE (ticket_id, sender_id, message_text, insert_date, archive) VALUES
(1, 3, 'Nie mogę się zalogować do systemu. Pokazuje błędne hasło.', CONVERT(DATETIME, '2024-02-16 11:05:00', 120), 0),
(1, 2, 'Czy próbowałaś zresetować hasło?', CONVERT(DATETIME, '2024-02-16 11:10:00', 120), 0),
(2, 3, 'Brak kursu na moim profilu.', CONVERT(DATETIME, '2024-02-16 11:35:00', 120), 0);

INSERT INTO MESSAGE_ATTACHMENT (message_id, attachment) VALUES
(1, 0xAABBCCDD),
(2, 0xEEFF0011);
