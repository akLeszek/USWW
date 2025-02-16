/*-- noinspection SqlResolveForFile
INSERT INTO users VALUES
(1, 'admin', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$/x2V5zGqcVN8c0S9MRvPjA', 'usww', 'admin', 1, 0),
(2, 'ales', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$XNmnuBoZ1PckufKdsvRuKA', 'Adrian', 'Leś', 1, 0),
(3, 'dmitnik', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$jxZI+x4UjiIYk9vQHDQ4XA', 'Dawid', 'Mitnik', 0, 0),
(4, 'awazon', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$wZMyx0so3wSokma72uSeUQ', 'Andrzej', 'Wazon', 0, 0),
(5, 'pkopytko', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$0E5fh1RieZmD+2LWe1YV6w', 'Paulina', 'Kopytko', 0, 0),
(6, 'strawnik', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$ArQx4MS4QCitHj/of2G5jg', 'Sylwia', 'Trawnik', 0, 0),
(7, 'mlapa', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$BYFLLkcxb2qHuGgKT/bUIQ', 'Magdalena', 'Łapa', 0, 0),
(8, 'cmichniewicz', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$SRcmZ0fnRpqN3gupHH5jVA', 'Czesław', 'Michniewicz', 0, 0),
(9, 'jbrzeczek', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$UI7MPZklKaFVvP7tJiC9AA', 'Jerzy', 'Brzęczek', 0, 0),
(10, 'zboniek', '$argon2i$v=19$m=16,t=2,p=1$Qm9iRHlsYW4$NqRxFtwCN/kKYwfkJ4KUQQ', 'Zbigniew', 'Boniek', 0, 0)
*/

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
('admin', 0x123456789ABCDEF, 'Jan', 'Kowalski', 0, '2024-02-16 10:00:00', 1, 3, 0),
('technician1', 0x123456789ABCDEF, 'Piotr', 'Nowak', 0, '2024-02-16 10:10:00', 2, 1, 0),
('student1', 0x123456789ABCDEF, 'Anna', 'Zielińska', 0, '2024-02-16 10:15:00', 3, 2, 0);

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
