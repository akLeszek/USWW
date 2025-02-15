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

-- Dodawanie przykładowych jednostek organizacyjnych
INSERT INTO ORGANIZATION_UNIT (idn, name)
VALUES ('WI', 'Wydział Informatyki'),
       ('WM', 'Wydział modelarstwa');

-- Dodawanie przykładowych kategorii zgłoszeń
INSERT INTO TICKET_CATEGORY (idn, name)
VALUES ('POPS', 'Podanie o przedłużenie sesji'),
       ('ZOSS', 'Zaświadczenie o statusie studenta');

INSERT INTO USER_GROUP (idn, name)
VALUES ('A', 'Administrator'),
       ('S', 'Student'),
       ('P', 'Pracownik Dziekanatu');

INSERT INTO TICKET_STATUS (idn, name)
VALUES ('N', 'Nowe'),
       ('W', 'W trakcie rozpatrywania'),
       ('Z', 'Zakończone');

-- Dodawanie przykładowych użytkowników
INSERT INTO USER (login, password, forename, surname, last_login, login_ban, archive, group_id,
                  organization_user_id)
VALUES ('admin', 'admin', 'Admin', 'USWW', '2024-02-10 08:30:00', 0, 0, 1, null),
       ('pracownik1', 'pracownik1', 'Marek', 'Wiśniewski', '2024-02-08 10:15:00', 0, 0, 3, 1),
       ('pracownik2', 'pracownik2', 'Andrzej', 'Fonfara', '2024-02-08 10:15:00', 0, 0, 3, 2),
       ('student1', 'student1', 'Jan', 'Kowalski', '2024-02-10 08:30:00', 0, 0, 2, 1),
       ('student2', 'student2', 'Anna', 'Nowak', '2024-02-09 14:50:00', 0, 0, 2, 1),
       ('student_ban', 'student_ban', 'Student', 'Ban', '2024-02-09 14:50:00', 1, 0, 2, 1);

-- Dodawanie przykładowych zgłoszeń
INSERT INTO TICKET (student_id, operator_id, category_id, status_id, inserted_date, change_date, archive)
VALUES (3, 2, 2, 1, '2024-02-05 09:00:00', '2024-02-06 12:00:00', 0),
       (4, 2, 3, 2, '2024-02-06 14:30:00', '2024-02-07 10:15:00', 0);

-- Dodawanie przykładowych wiadomości do zgłoszeń
INSERT INTO TICKET_MESSAGE (ticket_id, sender_id, message_text, inserted_date)
VALUES (1, 3, 'Czy mogę uzyskać informację na temat przedłużenia sesji?', '2024-02-05 09:15:00'),
       (1, 2, 'Tak, wniosek musi być zatwierdzony przez dziekana.', '2024-02-06 11:30:00'),
       (2, 4, 'Proszę o zaświadczenie o statusie studenta.', '2024-02-06 14:45:00'),
       (2, 2, 'Zaświadczenie zostało wysłane na e-mail.', '2024-02-07 10:00:00');
