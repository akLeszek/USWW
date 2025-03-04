INSERT INTO USER_GROUP (idn, name) VALUES
('ADMIN', 'Administratorzy'),
('TECH', 'Technicy'),
('STUDENT', 'Studenci');

INSERT INTO ORGANIZATION_UNIT (idn, name) VALUES
('IT', 'Dział IT'),
('DZIEKANAT', 'Dziekanat'),
('ADMIN', 'Administracja');

INSERT INTO TICKET_CATEGORY (idn, name) VALUES
('LOGOWANIE', 'Problemy z logowaniem'),
('KURS', 'Problemy z dostępem do kursów'),
('INNE', 'Inne zgłoszenia');

INSERT INTO TICKET_STATUS (idn, name) VALUES
('NOWE', 'Nowe'),
('W_TRAKCIE', 'W trakcie realizacji'),
('ZAMKNIETE', 'Zamknięte');

INSERT INTO USERS (login, password, forename, surname, login_ban, last_login, group_id, organization_unit_id, archive) VALUES
('admin', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Jan', 'Kowalski', 0, '2024-02-16 10:00:00', 1, 3, 0),
('technician1', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Piotr', 'Nowak', 0, '2024-02-16 10:10:00', 2, 1, 0),
('student1', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Anna', 'Zielińska', 0, '2024-02-16 10:15:00', 3, 2, 0);

INSERT INTO TICKET (operator_id, student_id, status_id, category_id, inserted_date, change_date, archive, title) VALUES
(2, 3, 1, 1, '2024-02-16 11:00:00', NULL, 0, 'Nie mogę się zalogować'),
(2, 3, 2, 2, '2024-02-16 11:30:00', '2024-02-16 12:00:00', 0, 'Brak dostępu do kursu'),
(2, 3, 3, 3, '2024-02-15 09:00:00', '2024-02-16 13:00:00', 1, 'Problem z aplikacją');

INSERT INTO TICKET_MESSAGE (ticket_id, sender_id, message_text, insert_date, archive) VALUES
(1, 3, 'Nie mogę się zalogować do systemu. Pokazuje błędne hasło.', '2024-02-16 11:05:00', 0),
(1, 2, 'Czy próbowałaś zresetować hasło?', '2024-02-16 11:10:00', 0),
(2, 3, 'Brak kursu na moim profilu.', '2024-02-16 11:35:00', 0);

INSERT INTO MESSAGE_ATTACHMENT (message_id, attachment) VALUES
(1, 0xAABBCCDD),  -- Przykładowy plik binarny
(2, 0xEEFF0011);
